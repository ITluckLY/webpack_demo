package com.dc.smarteam.modules.client.entity;

import com.dc.smarteam.common.persistence.DataEntity;

import java.util.List;


public class AllPath extends DataEntity<AllPath> {
    private static final long serialVersionUID = 9153459619261343950L;
    private List<String> allPath;

    public AllPath( List<String> allPath) {
        this.allPath = allPath;
    }
    public AllPath() {
    }

    public List<String> getAllPath() {
        return allPath;
    }

    public void setAllPath(List<String> allPath) {
        this.allPath = allPath;
    }

    @Override
    public String toString() {
        String str = allPath.toString();
        String result = str.substring(1,str.length()-1);
        return result;
    }
}
