package com.dc.smarteam.aspectCfg.cfgOperate;


/**
 * Created by xuchuang on 2018/5/25.
 * 配置底层逻辑服务类公共接口，实现获取配置实体类到XML文本的映射
 */
public interface ICfgService {

    //获取对应配置文件名
    String getCfgFileName();
    //获取节点类型
    String getNodeType();
    //获取备注信息
    String getSystem();

    //获取对应校验配置文件名
    // 后期再改
    // String getCfgPsFileName();




    //获取改动之后的值
    //获取当前逻辑实体对应的存储实体
    //String getWholeXml(CfgData curr);
    //将当前逻辑实体转换为存储实体
    //String getAddXml(CfgData curr);

    /**
     * 获取配置实体类对应的xml文本
     * @param curr  当前实体类
     * @param isNew 是否是新增实体:新增实体直接转换,非新增实体查询已存在对应实体并转换
     * @return
     */
    String getEntityXml(CfgData curr, boolean isNew);

}
