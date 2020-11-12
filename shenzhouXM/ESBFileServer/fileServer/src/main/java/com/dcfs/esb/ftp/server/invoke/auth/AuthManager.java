package com.dcfs.esb.ftp.server.invoke.auth;

import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.config.Cfg;
import com.dcfs.esb.ftp.server.file.EsbFileManager;
import com.dcfs.esb.ftp.server.file.EsbFilePath;
import com.dcfs.esb.ftp.server.file.UserPathAuth;
import com.dcfs.esb.ftp.server.invoke.node.NodesManager;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.server.tool.XMLDealTool;
import com.dcfs.esb.ftp.server.tool.XMLtoJSON;
import com.dcfs.esb.ftp.utils.JSONUtil;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AuthManager {
    private static final Logger log = LoggerFactory.getLogger(AuthManager.class);

    private static final String FILEPATH = Cfg.getFileCfg();
    private Document doc;
    private Element root;
    private String name;
    private String path;
    private String permession;//用户名称=权限,多个以逗号分隔 eg:uid=A,aaa=W,...
    private String xpath;

    private AuthManager() {
        try {
            doc = CachedCfgDoc.getInstance().loadFile();
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
        root = XMLDealTool.getRoot(doc);
    }

    public static AuthManager getInstance() {
        return new AuthManager();
    }

    /**
     * 处理前台报文
     *
     * @param jsonObject
     */
    private void load(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        name = MessDealTool.getString(data, "name");
        path = MessDealTool.getString(data, "path");
        permession = MessDealTool.getString(data, "permession");
        xpath = "/FileRoot/BaseFile[@name='" + name + "']";//NOSONAR
    }

    public ResultDto<String> add(JSONObject jsonObject) {
        load(jsonObject);

        if (null == name || name.equals("")) {
            return ResultDtoTool.buildError("用户目录不能为空");
        }

        if (null == path || path.equals("")) {
            return ResultDtoTool.buildError("用户路径不能为空");
        }

        Element e = (Element) root.selectSingleNode(xpath);
        if (null != e) {
            return ResultDtoTool.buildError("添加失败，存在重复目录");
        }

        Element baseFile = addBaseFile(name, path);

        if (null != permession) {
            String[] ps = permession.split(",");
            for (String param : ps) {
                int idx = param.indexOf('=');
                String userName = param.substring(0, idx).trim();
                if (userName.length() < 1) {
                    return ResultDtoTool.buildError("授权用户不能为空");
                }

                String type = param.substring(idx + 1).trim();
                if (type.isEmpty()) {
                    return ResultDtoTool.buildError("权限类型不能为空");
                }
                addGrant(baseFile, userName, type);
            }
        }

        String str = XMLDealTool.xmlWriter(doc, FILEPATH);

        EsbFileManager.reload();
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> del(JSONObject jsonObject) {
        load(jsonObject);

        Element baseFile = (Element) root.selectSingleNode(xpath);
        if (null == baseFile) {
            log.error("没有找到指定的目录信息#{}", xpath);//NOSONAR
            return ResultDtoTool.buildError("没有找到指定的目录信息");//NOSONAR
        }
        root.remove(baseFile);

        String str = XMLDealTool.xmlWriter(doc, FILEPATH);
        EsbFileManager.reload();
        return ResultDtoTool.buildSucceed(str);
    }

    /**
     * 全量更新permession
     *
     * @param jsonObject
     * @return
     */
    public ResultDto<String> update(JSONObject jsonObject) {//NOSONAR
        load(jsonObject);

        Element baseFile = (Element) root.selectSingleNode(xpath);
        if (null == baseFile) {
            log.error("没有找到指定的目录信息#{}", xpath);
            return ResultDtoTool.buildError("没有找到指定的目录信息");
        }

        if (null != path) {
            Attribute attr = baseFile.attribute("path");
            attr.setValue(path);
        }

        if (null != permession) {
            @SuppressWarnings("unchecked")
            List<Element> list = baseFile.elements();
            if (!list.isEmpty()) {
                for (Element e : list) {
                    baseFile.remove(e);
                }
            }

            if (permession.length() > 0) {
                String[] ps = permession.split(",");
                for (String param : ps) {
                    int idx = param.indexOf('=');
                    String userName = param.substring(0, idx).trim();

                    if (userName.isEmpty()) {
                        return ResultDtoTool.buildError("授权用户不能为空");
                    }

                    String type = param.substring(idx + 1).trim();
                    if (type.isEmpty()) {
                        return ResultDtoTool.buildError("权限类型不能为空");
                    }
                    addGrant(baseFile, userName, type);
                }
            }
        }
        String str = XMLDealTool.xmlWriter(doc, FILEPATH);
        EsbFileManager.reload();
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> query() {
        String str = XMLtoJSON.getJSONFromXMLEle(root);
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> selByName(JSONObject jsonObject) {
        load(jsonObject);
        Element baseFile = (Element) root.selectSingleNode(xpath);
        if (null == baseFile) {
            log.error("没有找到指定的目录信息#{}", xpath);
            return ResultDtoTool.buildError("没有找到指定的目录信息");
        }

        String str = XMLtoJSON.getJSONFromXMLEle(baseFile);
        return ResultDtoTool.buildSucceed(str);
    }

    /**
     * 增量添加或修改一个，不删除其他权限
     *
     * @param name
     * @param user
     * @param auth
     * @return
     */
    public String addAuth(String name, String user, String auth) {
        Element baseFile = (Element) root.selectSingleNode("/FileRoot/BaseFile[@name='" + name + "']");
        if (baseFile == null) baseFile = addBaseFile(name, name.substring(1));
        String str;
        Element grantEle = (Element) baseFile.selectSingleNode("grant[@user='" + user + "']");
        if (grantEle == null) {
            addGrant(baseFile, user, auth);
        } else {
            String type = grantEle.attributeValue("type");
            if (!StringUtils.equalsIgnoreCase(type, auth)) {
                grantEle.addAttribute("type", auth);
            }
        }
        str = XMLDealTool.xmlWriter(doc, FILEPATH);
        EsbFileManager.reload();
        return str;
    }

    private Element addBaseFile(String name, String path) {
        Element baseFile = XMLDealTool.addChild("BaseFile", root);
        XMLDealTool.addProperty("name", name, baseFile);
        XMLDealTool.addProperty("path", path, baseFile);
        return baseFile;
    }

    private Element addGrant(Element baseFile, String user, String auth) {
        Element grant = XMLDealTool.addChild("grant", baseFile);
        XMLDealTool.addProperty("user", user, grant);
        XMLDealTool.addProperty("type", auth, grant);
        return grant;
    }

    /**
     * 增量添加或修改一个或多个权限，不删除其他权限
     *
     * @param jsonObject
     * @return
     */
    public ResultDto<String> addAuth(JSONObject jsonObject) {
        load(jsonObject);
        String[] permArr = permession.split(",");
        String result = null;
        for (String perm : permArr) {
            String[] arr = perm.split("=");
            result = addAuth(name, arr[0].trim(), arr[1].trim());
        }
        return ResultDtoTool.buildSucceed(result);
    }

    public ResultDto<String> updateAuth(String name, String user, String auth) {
        Element grantEle = (Element) root.selectSingleNode("/FileRoot/BaseFile[@name='" + name + "']/grant[@user='" + user + "']");
        if (grantEle != null) {
            String type = grantEle.attributeValue("type");
            if (!StringUtils.equalsIgnoreCase(type, auth)) {
                grantEle.addAttribute("type", auth);
            }
            String str = XMLDealTool.xmlWriter(doc, FILEPATH);
            EsbFileManager.reload();
            return ResultDtoTool.buildSucceed(str);
        } else return ResultDtoTool.buildError("不存在该用户权限");
    }

    public ResultDto<String> updateAuth(JSONObject jsonObject) {
        load(jsonObject);
        String[] permArr = permession.split(",");
        ResultDto<String> resultDto = null;
        for (String perm : permArr) {
            String[] arr = perm.split("=");
            resultDto = updateAuth(name, arr[0].trim(), arr[1].trim());
        }
        if (resultDto == null) resultDto = ResultDtoTool.buildError("参数错误");
        return resultDto;
    }

    //删除用户目录权限
    public ResultDto<String> delAuth(String name, String user) {
        if (name != null) {
            Element grantEle = (Element) root.selectSingleNode("/FileRoot/BaseFile[@name='" + name + "']/grant[@user='" + user + "']");
            if (grantEle != null) {
                grantEle.getParent().remove(grantEle);
                String str = XMLDealTool.xmlWriter(doc, FILEPATH);
                EsbFileManager.reload();
                return ResultDtoTool.buildSucceed(str);
            } else {
                return ResultDtoTool.buildError("不存在该用户权限");
            }
        } else return ResultDtoTool.buildError("参数错误");
    }

    public ResultDto<String> delAuth(JSONObject jsonObject) {
        load(jsonObject);
        String[] permArr = permession.split(",");
        ResultDto<String> resultDto = null;
        for (String perm : permArr) {
            String[] arr = perm.split("=");
            resultDto = delAuth(name, arr[0]);
        }
        if (resultDto == null) resultDto = ResultDtoTool.buildError("参数错误");
        return resultDto;
    }

    /**
     * 查询一个用户可以操作哪些目录
     *
     * @param jsonObject name 用户名
     * @return JSONArray
     */
    public ResultDto<String> queryAuthDirOfUser(JSONObject jsonObject) {
        load(jsonObject);
        String userName = this.name;
        Map<String, List<UserPathAuth>> userPathAuthMap = EsbFileManager.getInstance().getUserPathAuthMap();
        List<UserPathAuth> userPathAuthList = userPathAuthMap.get(userName);
        if (userPathAuthList == null) return ResultDtoTool.buildSucceed("[]");
        JSONArray jsonArray = JSONArray.fromObject(userPathAuthList);
        String str = jsonArray.toString();

        return ResultDtoTool.buildSucceed(str);
    }

    /**
     * 查询一个目录可以被哪些用户操作
     *
     * @param jsonObject path 目录
     * @return
     */
    public ResultDto<String> queryAuthUserOfDir(JSONObject jsonObject) {
        load(jsonObject);
        String path2 = this.path;
        Map<String, EsbFilePath> basePathMap = EsbFileManager.getInstance().getBasePathMap();
        HashMap<String, Map<String, String>> authPathMap = new HashMap<>();
        if (StringUtils.isEmpty(path2) || "/".equals(path2)) {
            for (Map.Entry<String, EsbFilePath> entry : basePathMap.entrySet()) {
                authPathMap.put(entry.getKey(), entry.getValue().getFileAuth());
            }
        } else {
            int fromIndex = 1;
            while (true) {
                int index = path2.indexOf('/', fromIndex);
                if (index == -1) break;
                fromIndex = index + 1;
                String ppath = path2.substring(0, index);
                EsbFilePath esbFilePath = basePathMap.get(ppath);
                if (esbFilePath != null) authPathMap.put(ppath, esbFilePath.getFileAuth());
            }
            EsbFilePath esbFilePath = basePathMap.get(path2);
            if (esbFilePath != null) authPathMap.put(path2, esbFilePath.getFileAuth());
        }
        return ResultDtoTool.buildSucceed(JSONUtil.fromObject(authPathMap));
    }

    public ResultDto<String> queryAuthUserOfSys(JSONObject jsonObject) {
        load(jsonObject);
        String sysname = this.name;
        Map<String, List<UserPathAuth>> userPathAuthMap = EsbFileManager.getInstance().getUserPathAuthMap();
        HashMap<String, List<UserPathAuth>> sysPathAuthMap = new HashMap<>();
        for (Map.Entry<String, List<UserPathAuth>> entry : userPathAuthMap.entrySet()) {
            if (entry.getKey().startsWith(sysname + "_")) {
                sysPathAuthMap.put(entry.getKey(), entry.getValue());
            }
        }
        String str = JSONUtil.fromObject(sysPathAuthMap);
        return ResultDtoTool.buildSucceed(str);
    }

    public ResultDto<String> queryAllOrderbySys() {
        Set<String> sysSet = NodesManager.getInstance().listSystem();//找出所有有效system
        Map<String, List<UserPathAuth>> userPathAuthMap = EsbFileManager.getInstance().getUserPathAuthMap();
        HashMap<String, List<UserPathAuth>> sysPathAuthMap = new HashMap<>();
        for (String sysName : sysSet) {
            for (Map.Entry<String, List<UserPathAuth>> entry : userPathAuthMap.entrySet()) {
                if (entry.getKey().startsWith(sysName + "_")) {
                    sysPathAuthMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        String str = JSONUtil.fromObject(sysPathAuthMap);
        return ResultDtoTool.buildSucceed(str);
    }
}
