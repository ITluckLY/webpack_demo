package com.dcfs.esb.ftp.namenode.service;

import com.dcfs.esb.ftp.common.model.BizFileInfoMsg;
import com.dcfs.esb.ftp.common.model.BizFileMsg;
import com.dcfs.esb.ftp.namenode.spring.NameSpringContext;
import com.dcfs.esb.ftp.namenode.spring.core.entity.biz.BizFile;
import com.dcfs.esb.ftp.namenode.spring.core.repository.BizFileRepository;
import com.dcfs.esb.ftp.servcomm.dto.ResultDto;
import com.dcfs.esb.ftp.server.invoke.bizfile.BizFileServiceFace;
import com.dcfs.esb.ftp.server.tool.MessDealTool;
import com.dcfs.esb.ftp.server.tool.ResultDtoTool;
import com.dcfs.esb.ftp.utils.GsonUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangzbb on 2016/10/19.
 */
public class BizFileService implements BizFileServiceFace {
    private static final Logger log = LoggerFactory.getLogger(BizFileService.class);
    private static BizFileService ourInstance = new BizFileService();
    private String requestFilePath;

    public static BizFileService getInstance() {
        return ourInstance;
    }

    /**
     * 处理前台报文
     *
     * @param jsonObject
     */
    private void load(JSONObject jsonObject) {
        JSONObject data = jsonObject.getJSONObject("data");
        requestFilePath = MessDealTool.getString(data, "requestFilePath");
    }

    public ResultDto<String> selByRequestFilePath(JSONObject jsonObject) {
        ResultDto<String> resultDto;
        load(jsonObject);
        if (null == requestFilePath) {
            log.error("请求文件路径不能为空");
            resultDto = ResultDtoTool.buildError("请求文件路径不能为空");
            return resultDto;
        }
        BizFileRepository bizFileRepository = NameSpringContext.getInstance().getBizFileRepository();
        List<BizFile> bizFileList = bizFileRepository.findByRequestFilePath(requestFilePath);
        if (bizFileList.isEmpty()) {
            resultDto = ResultDtoTool.buildError("没有找到指定的文件信息");
            return resultDto;
        }
        BizFileMsg bizFileMsg = new BizFileMsg();
        ArrayList<BizFileInfoMsg> fileInfoList = new ArrayList<>();
        BizFileInfoMsg bizFileInfoMsg;
        for (BizFile bizFile : bizFileList) {
            Long fileSize = bizFile.getFileSize();
            String nodeName = bizFile.getNodeName();
            String systemName = bizFile.getSystemName();
            int state = bizFile.getState();
            String fileMd5 = bizFile.getFileMd5();

            //TODO
            bizFileMsg.setFilePath(bizFile.getFilePath());
            bizFileMsg.setFileSize(fileSize);
            bizFileMsg.setFileMD5(fileMd5);
            bizFileInfoMsg = new BizFileInfoMsg();
            bizFileInfoMsg.setNodeName(nodeName);
            bizFileInfoMsg.setSystemName(systemName);
            bizFileInfoMsg.setState(state);
            bizFileInfoMsg.setFileVersion(bizFile.getFileVersion());
            fileInfoList.add(bizFileInfoMsg);
        }
        bizFileMsg.setFileInfo(fileInfoList);
        String respJSON = GsonUtil.toJson(bizFileMsg);
        return ResultDtoTool.buildSucceed(respJSON);
    }
}
