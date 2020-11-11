/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.client.service;

import com.dc.smarteam.common.adapter.TCPAdapter;
import com.dc.smarteam.common.json.GsonUtil;
import com.dc.smarteam.common.json.ResultDto;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.vo.ZTreeNode;
import com.dc.smarteam.modules.client.dao.ClientFileDao;
import com.dc.smarteam.modules.client.entity.ClientFile;
import com.dc.smarteam.modules.client.version.ClientVersionCli;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * 文件管理Service
 *
 * @author liwang
 * @version 2016-01-12
 */
@Service
@Transactional(readOnly = true)
public class ClientFileService extends CrudService<ClientFileDao, ClientFile> {
    private static final Logger log = LoggerFactory.getLogger(ClientFileService.class);
    //TODO 从配置文件中读取
    private List<String> basePath = new ArrayList<>();

    public List<String> getBasePath() {
        return basePath;
    }

    public void setBasePath(List<String> basePath) {
        this.basePath = basePath;
    }

    //根据文件路径和名称生成一个字符串(防止中文字符)
    public static String generateFileId(File file) {
        String path = file.getPath() + File.separator + file.getName();
        String encodePath = "";
        try {
            encodePath = URLEncoder.encode(path, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
        }
        return encodePath;
    }

    @Override
    public ClientFile get(String id) {
        return super.get(id);
    }

    @Override
    public List<ClientFile> findList(ClientFile clientFile) {
        return super.findList(clientFile);
    }

    @Override
    public Page<ClientFile> findPage(Page<ClientFile> page, ClientFile clientFile) {
        return super.findPage(page, clientFile);
    }

    @Override
    @Transactional(readOnly = false)
    public void save(ClientFile clientFile) {
        super.save(clientFile);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(ClientFile clientFile) {
        super.delete(clientFile);
    }

    public List<ZTreeNode> getFileTree() throws Throwable {//NOSONAR
        List<ZTreeNode> list = new ArrayList<ZTreeNode>();
        String sepStr = File.separator;
        if ("\\".equals(sepStr)) {
            sepStr = "\\\\";
        }
        int j = 1;
        for(String path:basePath){
            String npath = path.replaceAll("\\[|\\]","");
            String[] dirs = npath.split(sepStr);
            //构建根节点
            ZTreeNode root = new ZTreeNode();
            list.add(root);
            root.setId(String.valueOf(j));
            j++;
            root.setPid("0");
            root.setName(npath.substring(npath.lastIndexOf("/")));
            root.setPids("");
            String parentId = root.getId();
            //生成目录节点
            for (int i = 1; i < dirs.length; i++) {
                String dir = dirs[i];
                if (StringUtils.isNotEmpty(dir)) {
                    ZTreeNode node = new ZTreeNode();
                    list.add(node);
                    node.setId(UUID.randomUUID().toString());
                    node.setPid(parentId);
                    node.setParentTId(parentId);
                    node.setName(dir);
                    node.setPids("");
                    parentId = node.getId();
                }
            }
            //查询路径下所有文件夹
            File file = new File(npath);
            File[] children = file.listFiles();
            if(children != null){
                for (int i = 0; i < children.length; i++) {
                    File child = children[i];
                    if (child.isDirectory()) {
                        ZTreeNode node = new ZTreeNode();
                        list.add(node);
                        node.setId(generateFileId(file));
                        node.setName(child.getName());
                        node.setPid(parentId);
                        node.setParentTId(parentId);
                        node.setPids("");
                        node.setPath(npath + File.separator + child.getName());
                    }
                }
            }
        }
        return list;
    }

    //获取目录下文件
    public void getContent(String path, Page<ClientFile> page) {
        List<ClientFile> clientFiles = page.getList();
        File file = new File(path);
        File[] files = file.listFiles();
        for (File subFile : files) {
            if (subFile.isFile()) {
                ClientFile clientFile = new ClientFile(subFile);
                clientFiles.add(clientFile);
            }
        }
    }

    public String repush2Datenode(ClientFile clientFile){

        String result = "重推成功";
        return result;
    }
}