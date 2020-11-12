package com.dcfs.esb.ftp.server.file;

import com.dcfs.esb.ftp.common.error.FtpErrCode;
import com.dcfs.esb.ftp.common.error.FtpException;
import com.dcfs.esb.ftp.server.config.CachedCfgDoc;
import com.dcfs.esb.ftp.server.config.FtpConfig;
import com.dcfs.esb.ftp.utils.FileUtil;
import com.dcfs.esb.ftp.utils.GsonUtil;
import com.dcfs.esc.ftp.comm.exception.NestedRuntimeException;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * 文件系统管理工厂
 */
public class EsbFileWorker {
    public static final String OP_W = "W";
    public static final String OP_R = "R";
    public static final String OP_A = "A";
    private static final Logger log = LoggerFactory.getLogger(EsbFileWorker.class);
    private static final Object lock = new Object();
    private static EsbFileWorker instance = null;
    private HashMap<String, EsbFilePath> basePathMap = new HashMap<>();
    private HashMap<String, List<UserPathAuth>> userPathAuthMap = new HashMap<>();
    private String rootPath;

    /**
     * @return
     */
    public static EsbFileWorker getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    reload();
                }
            }
        }
        return instance;
    }

    /**
     *
     */
    public static void reload() {
        try {
            CachedCfgDoc.getInstance().reloadFile();
        } catch (FtpException e) {
            throw new NestedRuntimeException(e.getMessage(), e);
        }
        EsbFileWorker inst = new EsbFileWorker();
        inst.load();
        instance = inst;
    }

    /**
     * @param fileName
     * @return
     * @throws FtpException
     */
    public static String getFileBaseName(String fileName) throws FtpException {
        if (fileName == null) return null;
        String baseName = null;
        try {
            String[] list = fileName.split("/");
            baseName = "/" + list[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("文件名字必须添加服务器文件路径", e);
            throw new FtpException(FtpErrCode.FILE_NOT_FOUND_ERROR);
        }
        return baseName;
    }

    /**
     *
     */
    public void load() {
        log.debug("加载文件权限信息...");
        rootPath = FtpConfig.getInstance().getFileRootPath();

        try {
            Document doc = CachedCfgDoc.getInstance().loadFile();
            Element root = doc.getRootElement();
            Element fileInfo = null;
            for (Iterator<?> i = root.elementIterator(); i.hasNext(); ) {
                fileInfo = (Element) i.next();
                String name = fileInfo.attributeValue("name");
                String path = fileInfo.attributeValue("path");
                //path = rootPath + "/" + path;//NOSONAR
                EsbFilePath filePath = new EsbFilePath(name, path);
                addBaseFilePath(filePath);
                List<?> list = fileInfo.elements("grant");
                for (Object aList : list) {
                    Element grant = (Element) aList;
                    //String sysname = grant.attributeValue("sysname");//NOSONAR
                    String user = grant.attributeValue("user");
                    String type = grant.attributeValue("type");
                    //filePath.addFileAuth("esb", EsbFileManage.OP_A);//NOSONAR
                    //String sysuser = sysname + "_" + user;//NOSONAR
                    String sysuser = user;
                    filePath.addFileAuth(sysuser, type);
                    addUserPathAuth(new UserPathAuth(sysuser, name, path, type));
                }
            }
            sortUserPathAuth();
            printUserPathAuth();
        } catch (Exception e) {
            log.error("加载文件权限信息失败", e);
        }
        log.debug("加载文件权限信息成功");
    }

    /**
     * 倒序排序UserPathAuth.name，实现权限就近原则
     */
    private void sortUserPathAuth() {
        for (Map.Entry<String, List<UserPathAuth>> entry : userPathAuthMap.entrySet()) {
            List<UserPathAuth> pathAuths = entry.getValue();
            Collections.sort(pathAuths, new Comparator<UserPathAuth>() {
                @Override
                public int compare(UserPathAuth o1, UserPathAuth o2) {
                    return -o1.getName().compareTo(o2.getName());//NOSONAR
                }
            });
        }
    }

    private void printUserPathAuth() {
        for (Map.Entry<String, List<UserPathAuth>> entry : userPathAuthMap.entrySet()) {
            String uid = entry.getKey();
            List<UserPathAuth> pathAuths = entry.getValue();
            for (UserPathAuth pathAuth : pathAuths) {
                if (log.isDebugEnabled()) log.debug("UserPathAuth#uid:{},pathAuth:{}", uid, GsonUtil.toJson(pathAuth));
            }
        }
    }

    private void addUserPathAuth(UserPathAuth userPathAuth) {
        List<UserPathAuth> list = userPathAuthMap.get(userPathAuth.getUser());
        if (list == null) {
            list = new ArrayList<>();
            userPathAuthMap.put(userPathAuth.getUser(), list);
        }
        list.add(userPathAuth);
    }

    /**
     * @param filePath
     */
    public void addBaseFilePath(EsbFilePath filePath) {
        if (filePath != null) basePathMap.put(filePath.getFileName(), filePath);
    }

    /**
     * @param filePath
     * @param user
     * @param op
     * @return
     * @throws FtpException
     */
    public boolean isAuthToUser(String filePath, String user, String op) throws FtpException {//NOSONAR
        //return checkFileAuth(filePath,user,op);//NOSONAR
        return checkUserAuth(filePath, user, op);
    }

    private boolean checkFileAuth(String filePath, String user, String op) throws FtpException {//NOSONAR
        String baseName = getFileBaseName(filePath);
        EsbFilePath file = basePathMap.get(baseName);
        if (file != null)
            return file.checkFileAuth(user, op);
        else {
            log.error("文件[{}]不存在", filePath);
            return false;
        }
    }

    /**
     * 实现权限就近原则
     *
     * @param filePath
     * @param user
     * @param op
     * @return
     * @throws FtpException
     */
    private boolean checkUserAuth(String filePath, String user, String op) throws FtpException {//NOSONAR
        //if (filePath.startsWith("TV")) return true;//NOSONAR
        List<UserPathAuth> list = userPathAuthMap.get(user);
        if (list == null) {
            log.debug("UserPathAuth列表为null#filePath:{},user:{},op:{}", filePath, user, op);
            return false;
        }
        for (UserPathAuth auth : list) {
            String fileName = auth.getName();
            if (fileName.charAt(fileName.length() - 1) != '/') fileName = fileName + '/';
            if (filePath.startsWith(fileName)) {
                String authType = auth.getAuthType();
                if (StringUtils.equalsIgnoreCase(authType, op) || StringUtils.equalsIgnoreCase(EsbFileWorker.OP_A, authType)) {
                    log.debug("UserPathAuth校验通过#filePath:{},user:{},op:{}", filePath, user, op);
                    return true;
                }
            }
        }
        log.debug("UserPathAuth校验不通过#filePath:{},user:{},op:{}", filePath, user, op);
        return false;
    }

    public String getFileAbsolutePath(String fileName) {
        String separator = fileName.charAt(0) == '/' ? "" : File.separator;
        String fileAbsoluteName = rootPath + separator + fileName;
        return FileUtil.convertToLocalPath(fileAbsoluteName);
    }

    /**
     * 判断是否存在逻辑目录的配置
     *
     * @param dir
     * @return
     */
    public boolean hasDir(String dir) {
        return basePathMap.containsKey(dir);
    }

    public Map<String, EsbFilePath> getBasePathMap() {
        return basePathMap;
    }

    public Map<String, List<UserPathAuth>> getUserPathAuthMap() {
        return userPathAuthMap;
    }
}
