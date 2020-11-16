package com.dcfs.esb.ftp.process.handler;

import com.dcfs.esb.ftp.common.scrt.Md5Alg;
import com.dcfs.esc.ftp.comm.constant.SysConst;
import com.dcfs.esc.ftp.comm.dto.FileDownloadDataReqDto;
import com.dcfs.esc.ftp.comm.dto.FileDownloadDataRspDto;
import com.dcfs.esc.ftp.comm.util.IOUtil;
import com.dcfs.esc.ftp.datanode.context.DownloadContextBean;
import com.dcfs.esc.ftp.datanode.process.ProcessHandlerContext;
import com.dcfs.esc.ftp.datanode.process.handler.adapter.DownloadProcessHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.RandomAccessFile;

/**
 * Created by mocg on 2017/6/7.
 */
public class DownloadFileByRafHandler extends DownloadProcessHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(DownloadFileByRafHandler.class);
    private DownloadContextBean cxtBean;
    private RandomAccessFile raf;
    private Md5Alg md5Alm;

    @Override
    public void start(ProcessHandlerContext ctx) throws Exception {
        cxtBean = ctx.getChannelContext().cxtBean();
        raf = cxtBean.getRaf();
        if (raf == null) {
            String absFilePath = cxtBean.getAbsFilePath();
            raf = new RandomAccessFile(absFilePath, "r");
            cxtBean.setRaf(raf);
        }
        md5Alm = cxtBean.getMd5Alm();
    }

    @Override
    public Object process(ProcessHandlerContext ctx, Object msg) throws Exception {
        FileDownloadDataReqDto reqDto = ctx.requestObj(FileDownloadDataReqDto.class);
        FileDownloadDataRspDto rspDto = ctx.responseObj(FileDownloadDataRspDto.class);

        raf = cxtBean.getRaf();
        //to-do
        if (raf == null) {
            raf = new RandomAccessFile(reqDto.getServerAbsFileName(), "r");
        }
        long position = reqDto.getPosition();
        if (log.isTraceEnabled()) {
            log.trace("nano:{}#flowNo:{}#文件下载请求#position:{}", cxtBean.getNano(), cxtBean.getFlowNo(), position);
        }
        byte[] buf = cxtBean.getBuf();
        int toReadLen = buf.length;
        if (reqDto.getPieceNum() > SysConst.MIN_PIECE_NUM) {
            toReadLen = Math.min(toReadLen, reqDto.getPieceNum());
        }
        raf.seek(position);
        int read = raf.read(buf, 0, toReadLen);
        rspDto.setReadLen(read);
        byte[] fileCont;
        if (read == -1) {
            fileCont = null;
        } else if (read == buf.length) {
            fileCont = buf;
        } else {
            fileCont = new byte[read];
            System.arraycopy(cxtBean.getBuf(), 0, fileCont, 0, read);
        }
        if (read > 0) md5Alm.update(fileCont);
        rspDto.fileCont(fileCont);
        rspDto.setCompressMode(null);
        rspDto.setScrt(false);
        rspDto.setPosition(position);//返回传过来的,由于客户端控制位置
        cxtBean.setReadLen(read);

        //md5
        boolean isLast = read == -1 || read < toReadLen;

        //生成md5
        String fileMd5 = null;
        if (isLast) fileMd5 = md5Alm.digestAndString();
        rspDto.setMd5(fileMd5);
        rspDto.setLastChunk(isLast);
        rspDto.setEnd(isLast);
        return msg;
    }

    @Override
    public void clean(ProcessHandlerContext ctx) {
        if (raf != null) {
            IOUtil.closeQuietly(raf);
            raf = null;
        }
        cxtBean = null;
        md5Alm = null;
    }
}
