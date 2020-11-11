/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.servicenode.dao;

import com.dc.smarteam.common.persistence.CrudDao;
import com.dc.smarteam.common.persistence.annotation.MyBatisDao;
import com.dc.smarteam.modules.servicenode.entity.FtServiceNode;
import com.dc.smarteam.modules.sysinfo.entity.FtSysInfo;

import java.util.List;

/**
 * 节点管理DAO接口
 *
 * @author liwang
 * @version 2016-01-11
 */
@MyBatisDao
public interface FtServiceNodeDao extends CrudDao<FtServiceNode> {


    //20160630添加选择系统菜单，获取到node集合
    public List<FtServiceNode> findListByFtSysInfo(FtSysInfo ftSysInfo);

    //20160707添加一条查询所有的节点的page
   /* public List<FtServiceNode> findAllPage(FtServiceNode ftServiceNode);*/
}