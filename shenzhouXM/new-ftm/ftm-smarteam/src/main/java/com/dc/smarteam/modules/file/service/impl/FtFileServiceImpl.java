package com.dc.smarteam.modules.file.service.impl;


import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.service.CrudService;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.vo.ZTreeNode;
import com.dc.smarteam.modules.file.dao.FtFileDao;
import com.dc.smarteam.modules.file.entity.FtFile;
import com.dc.smarteam.modules.file.service.FtFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文件管理Service
 *
 * @author liwang
 * @version 2016-01-12
 */
@Service("FtFileServiceImpl")
@Transactional(readOnly = true)
public class FtFileServiceImpl extends CrudService<FtFileDao, FtFile> implements FtFileService {
    private static final Logger log = LoggerFactory.getLogger(com.dc.smarteam.modules.file.service.FtFileService.class);
    //TODO 从配置文件中读取

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
    public FtFile get(String id) {
        return super.get(id);
    }

    @Override
    public List<FtFile> findList(FtFile ftFile) {
        return super.findList(ftFile);
    }

    @Override
    public Page<FtFile> findPage(Page<FtFile> page, FtFile ftFile) {
        return super.findPage(page, ftFile);
    }

    @Override
    @Transactional(readOnly = false)
    public void save(FtFile ftFile) {
        super.save(ftFile);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(FtFile ftFile) {
        super.delete(ftFile);
    }

    public List<ZTreeNode> getFileTree() throws Throwable {//NOSONAR
        List<ZTreeNode> list = new ArrayList<ZTreeNode>();
        String sepStr = File.separator;
        if ("\\".equals(sepStr)) {
            sepStr = "\\\\";
        }
        String[] dirs = basePath.split(sepStr);
        //构建根节点
        ZTreeNode root = new ZTreeNode();
        list.add(root);
        root.setId("1");
        root.setPid("0");
        root.setName(dirs[0]);
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
        File file = new File(basePath);
        File[] children = file.listFiles();
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
                node.setPath(basePath + File.separator + child.getName());
            }
        }
        return list;
    }

    //获取目录下文件
    public void getContent(String path, Page<FtFile> page) {
        List<FtFile> ftFiles = page.getList();
        File file = new File(path);
        File[] files = file.listFiles();
        for (File subFile : files) {
            if (subFile.isFile()) {
                FtFile ftFile = new FtFile(subFile);
                ftFiles.add(ftFile);
            }
        }
    }
}
