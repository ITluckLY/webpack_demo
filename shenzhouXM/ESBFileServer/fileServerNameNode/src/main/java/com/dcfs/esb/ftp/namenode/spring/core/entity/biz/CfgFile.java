package com.dcfs.esb.ftp.namenode.spring.core.entity.biz;

import com.dcfs.esb.ftp.spring.core.entity.BaseEntity;

import javax.persistence.*;

/**
 * Created by huangzbb on 2016/8/31.
 */
@Entity
@Table(name = "biz_cfg_file")
public class CfgFile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String filename;
    @Lob
    private String content;
    private String filetype;
    private int filesize;
    private String system;
    private String nodetype;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public int getFilesize() {
        return filesize;
    }

    public void setFilesize(int size) {
        this.filesize = size;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getNodetype() {
        return nodetype;
    }

    public void setNodetype(String nodetype) {
        this.nodetype = nodetype;
    }
}
