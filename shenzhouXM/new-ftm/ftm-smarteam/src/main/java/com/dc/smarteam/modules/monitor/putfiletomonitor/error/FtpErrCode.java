package com.dc.smarteam.modules.monitor.putfiletomonitor.error;

import java.net.SocketException;
import java.util.HashMap;

/**
 * 文件服务错误代码类
 */
public class FtpErrCode {
    private FtpErrCode() {
    }

    public static final String SUCC = "0000";
    public static final String FAIL = "9999";
    public static final String UNDEFINED = "UNDEF";

    //00xx socket异常
    public static final String SOCKET_ERROR = "TNCFTSE0001";
    public static final String SOCKET_CONNECT_ERROR = "TNCFTSE0002";
    public static final String SOCKET_CLOSE_ERROR = "TNCFTSE0003";
    public static final String SOCKET_TIME_OUT_ERROR = "TNCFTSE0004";
    public static final String IO_EXCEPTION = "TIOFTSE0005";
    public static final String WRITE_ACK_ERROR = "TNCFTSE0006";
    public static final String READ_ACK_ERROR = "TNCFTSE0007";
    public static final String TOO_LONG_FRAME_ERR = "TBLFTSE0008";
    public static final String CONNECT_EXCEPTION = "TNCFTSE0009";
    //05xx 文件异常
    public static final String FILE_OPERATE_ERROR = "TBLFTSE0501";
    public static final String FILE_NOT_FOUND_ERROR = "TDCFTSE0502";
    public static final String FILE_UP_ERROR = "TBLFTSE0503";
    public static final String FILE_DOWN_ERROR = "TBLFTSE0504";
    public static final String FILE_CLOSE_ERROR = "TBLFTSE0505";
    public static final String FILE_DELETE_ERROR = "TBLFTSE0506";
    public static final String FILE_RENAME_ERROR = "TBLFTSE0507";
    public static final String FILE_CHECK_ERROR = "TCEFTSE0508";
    public static final String FILE_READ_ERROR = "TIOFTSE0509";
    public static final String FILE_WRITE_ERROR = "TIOFTSE0510";
    public static final String NOT_FILE_ERROR = "TBLFTSE0511";
    public static final String OUT_OF_SIZE_ERROR = "TCEFTSE0512";
    public static final String CFG_FILE_DELETE_ERROR = "TBLFTSE0513";
    public static final String TMP_FILE_DELETE_ERROR = "TBLFTSE0514";
    public static final String FILE_LAST_PIECE_ERROR = "TBLFTSE0515";
    public static final String CFG_FILE_LOAD_ERROR = "TBLFTSE0516";
    public static final String SAVE_CONFIG_ERR = "TBLFTSE0517";
    public static final String CFG_FILE_RENAME_ERROR = "TBLFTSE0518";
    public static final String UPLOAD_TMP_FILE_NOT_EXIST = "TDCFTSE0519";
    public static final String CFG_FILE_OCCUPIED_ERROR = "TDOFTSE0520";
    public static final String DATA_LEN_GREATER_FILE_SIZE = "TBLFTSE0521";
    public static final String FILE_PACK_ERROR = "TBLFTSE0522";
    public static final String FILE_UNPACK_ERROR = "TBLFTSE0523";
    //10xx 文件锁
    public static final String FILE_ERROR = "TBLFTSE1001";
    public static final String LOCK_FILE_ERROR = "TBLFTSE1002";
    public static final String LOCK_FILE_LOCKED_ERROR = "TBLFTSE1003";
    public static final String LOCK_FILE_UNOCK_ERROR = "TBLFTSE1004";
    //15xx 转码异常
    public static final String CONVERT_FILE_ERROR = "TBLFTSE1501";
    public static final String SCRT_ENCRYPT_ERROR = "TBLFTSE1502";
    public static final String SCRT_DECRYPT_ERROR = "TBLFTSE1502";
    //20xx 认证信息异常
    public static final String IP_CHECK_FAILED = "TBLFTSE2001";
    public static final String AUTH_PWD_FAILED = "TPWFTSE2002";//NOSONAR
    public static final String AUTH_DIR_FAILED = "TACFTSE2003";
    public static final String AUTH_TRAN_CODE_FAILED = "TACFTSE2004";
    public static final String AUTH_ORIGINL_FILE_NAME_FAILED = "TACFTSE2005";
    public static final String AUTH_USER_FAILED = "TACFTSE2006";
    public static final String UNKNOWN_TRAN_CODE = "TDCFTSE2007";
    public static final String SYSNAME_NOT_SAME = "TDCFTSE2008";
    public static final String API_VERSION_NOT_SAME = "TDCFTSE2009";
    public static final String MOUNT_CHECK_FAILED = "TCEFTSE2010";
    public static final String NOT_SUPPORTED_MOUNT = "TBLFTSE2011";
    public static final String CLIENT_AUTH_USER_FAILED = "TBLFTSE2012";
    public static final String FILE_SIZE_OUT_OF_LIMIT = "TBLFTSE2013";
    public static final String FILE_SIZE_OUT_OF_CAPACITY = "TBLFTSE2014";
    public static final String PATH_CONTAINS_JUMP_SYMBOL = "TBLFTSE2015";
    public static final String AUTH_SEQ_FAILED = "TACFTSE2016";
    public static final String NODE_ISOLSTATE = "TACFTSE2017";
    //25xx 服务器执行异常
    public static final String ENCRYPT_ERROR = "TSSFTSE2501";
    public static final String DECRYPT_ERROR = "TSSFTSE2502";
    public static final String LOAD_CONFIG_FILE_ERROR = "TBLFTSE2503";
    public static final String SAVE_CONFIG_FILE_ERROR = "TBLFTSE2504";
    public static final String HEAD_XML_ERROR = "TBLFTSE2505";
    public static final String EXEC_CMD_ERROR = "TBLFTSE2506";
    public static final String TRANSF_CONNECT_ERROR = "TNCFTSE2507";
    public static final String READ_REQ_LENGTH_ERROR = "TIOFTSE2508";
    public static final String INVAILD_ROUTE_RULE = "TCEFTSE2509";
    public static final String NO_TOKEN = "TFCFTSE2510";
    public static final String DTO_PARSE_ERROR = "TBLFTSE2511";
    public static final String DTO_VERSION_INCOMPATIBLE = "TBLFTSE2512";
    public static final String DONT_MACTH_DTO_TYPE = "TBLFTSE2513";
    public static final String CHUNK_TYPE_DONT_MACTH = "TBLFTSE2514";
    //30xx 业务异常
    public static final String READ_FILE_MSG_BEAN_ERROR = "TIOFTSE3001";
    public static final String DEL_SAME_FILE_FROM_OTHERS_ERROR = "TDOFTSE3002";
    public static final String FLOW_ERROR = "TFCFTSE3003";
    public static final String BASE_SERVICE_ERROR = "TBLFTSE3004";
    public static final String RESOURCE_NOT_ENOUGH = "TBLFTSE3007";
    public static final String TASK_RESOURCE_NOT_ENOUGH = "TBLFTSE3008";
    public static final String TOKEN_RESOURCE_NOT_ENOUGH = "TBLFTSE3009";
    public static final String NODE_NOT_FOUND_ERROR = "TDCFTSE3011";
    public static final String FILE_MD5_VALID_FAIL = "TCEFTSE3012";
    public static final String READ_DTO_TRY_COUNT_OUT_OF_LIMIT = "TBLFTSE3013";
    //35xx java异常
    public static final String CERTIFICATE_EXCEPTION = "TBLFTSE3501";
    public static final String INTERRUPTED_EXCEPTION = "TBLFTSE3502";
    public static final String J_RUNTIME_EXCEPTION = "TBLFTSE3503";

    private static HashMap<String, String> errorCodeMap = new HashMap<String, String>();

    static {
        // socket 异常
        errorCodeMap.put(SOCKET_ERROR, "Socket错误");
        errorCodeMap.put(SOCKET_CONNECT_ERROR, "连接服务器异常");
        errorCodeMap.put(SOCKET_CLOSE_ERROR, "关闭Socket流异常");
        errorCodeMap.put(SOCKET_TIME_OUT_ERROR, "Socket通讯超时出错");
        errorCodeMap.put(IO_EXCEPTION, "IO异常");
        errorCodeMap.put(WRITE_ACK_ERROR, "发送Ack异常");
        errorCodeMap.put(READ_ACK_ERROR, "接收Ack异常");
        errorCodeMap.put(TOO_LONG_FRAME_ERR, "分片过大");
        errorCodeMap.put(CONNECT_EXCEPTION, "连接异常");

        // 文件异常
        errorCodeMap.put(FILE_OPERATE_ERROR, "文件操作异常");
        errorCodeMap.put(FILE_NOT_FOUND_ERROR, "文件不存在");
        errorCodeMap.put(FILE_UP_ERROR, "文件上传错误");
        errorCodeMap.put(FILE_DOWN_ERROR, "下载错误");
        errorCodeMap.put(FILE_CLOSE_ERROR, "文件关闭错误");
        errorCodeMap.put(FILE_DELETE_ERROR, "文件删除失败");
        errorCodeMap.put(CFG_FILE_DELETE_ERROR, "配置文件删除失败");
        errorCodeMap.put(TMP_FILE_DELETE_ERROR, "临时文件删除失败");
        errorCodeMap.put(FILE_RENAME_ERROR, "文件重命名出错");
        errorCodeMap.put(FILE_CHECK_ERROR, "文件Md5校验出错");
        errorCodeMap.put(FILE_READ_ERROR, "文件读取出错");
        errorCodeMap.put(FILE_WRITE_ERROR, "文件写入出错");
        errorCodeMap.put(NOT_FILE_ERROR, "路径非文件错误");
        errorCodeMap.put(OUT_OF_SIZE_ERROR, "超过文件大小限制");
        errorCodeMap.put(FILE_LAST_PIECE_ERROR, "文件传输中断");
        errorCodeMap.put(CFG_FILE_LOAD_ERROR, "配置文件加载失败");
        errorCodeMap.put(SAVE_CONFIG_ERR, "配置文件保存失败");
        errorCodeMap.put(CFG_FILE_RENAME_ERROR, "配置文件重命名出错");
        errorCodeMap.put(UPLOAD_TMP_FILE_NOT_EXIST, "上传的临时文件不存在");
        errorCodeMap.put(CFG_FILE_OCCUPIED_ERROR, "配置文件被占用");
        errorCodeMap.put(DATA_LEN_GREATER_FILE_SIZE, "数据长度大于文件大小");
        errorCodeMap.put(FILE_PACK_ERROR, "文件打包异常");
        errorCodeMap.put(FILE_UNPACK_ERROR, "文件解包异常");

        //文件锁
        errorCodeMap.put(FILE_ERROR, "文件处理失败");
        errorCodeMap.put(LOCK_FILE_ERROR, "锁住文件失败");
        errorCodeMap.put(LOCK_FILE_LOCKED_ERROR, "锁住锁文件失败");
        errorCodeMap.put(LOCK_FILE_UNOCK_ERROR, "解锁锁文件失败");

        // 转码异常
        errorCodeMap.put(CONVERT_FILE_ERROR, "转换文件出错");
        errorCodeMap.put(SCRT_ENCRYPT_ERROR, "加密异常");
        errorCodeMap.put(SCRT_DECRYPT_ERROR, "解密异常");

        // 认证信息异常
        errorCodeMap.put(AUTH_USER_FAILED, "用户没有权限");
        errorCodeMap.put(IP_CHECK_FAILED, "非法的用户地址");
        errorCodeMap.put(AUTH_PWD_FAILED, "用户名和密码检验不通过");
        errorCodeMap.put(AUTH_DIR_FAILED, "用户没有目录权限");
        errorCodeMap.put(AUTH_TRAN_CODE_FAILED, "用户没有交易码权限");
        errorCodeMap.put(AUTH_ORIGINL_FILE_NAME_FAILED, "没有权限使用原文件名");
        errorCodeMap.put(UNKNOWN_TRAN_CODE, "未知的交易码");
        errorCodeMap.put(SYSNAME_NOT_SAME, "系统名称不一致");
        errorCodeMap.put(API_VERSION_NOT_SAME, "API客户端版本与服务端的API版本不一致");
        errorCodeMap.put(MOUNT_CHECK_FAILED, "挂载校验不通过");
        errorCodeMap.put(NOT_SUPPORTED_MOUNT, "节点不支持挂载功能");
        errorCodeMap.put(CLIENT_AUTH_USER_FAILED, "客户端的用户权限校验不通过");
        errorCodeMap.put(FILE_SIZE_OUT_OF_LIMIT, "上传文件大小超过服务端限制");
        errorCodeMap.put(FILE_SIZE_OUT_OF_CAPACITY, "上传文件大小超过服务端剩余容量");
        errorCodeMap.put(PATH_CONTAINS_JUMP_SYMBOL, "路径包含跳转符号");
        errorCodeMap.put(AUTH_SEQ_FAILED, "权限SEQ校验失败");
        errorCodeMap.put(NODE_ISOLSTATE, "隔离节点不接受传输请求");

        // 服务器执行异常
        errorCodeMap.put(ENCRYPT_ERROR, "信息解密错误");
        errorCodeMap.put(DECRYPT_ERROR, "信息解密错误");
        errorCodeMap.put(LOAD_CONFIG_FILE_ERROR, "加载配置文件出错");
        errorCodeMap.put(SAVE_CONFIG_FILE_ERROR, "保存配置文件出错");
        errorCodeMap.put(HEAD_XML_ERROR, "XML文件格式错误");
        errorCodeMap.put(EXEC_CMD_ERROR, "执行系统命令出错");
        errorCodeMap.put(TRANSF_CONNECT_ERROR, "链接转发服务器异常");
        errorCodeMap.put(READ_REQ_LENGTH_ERROR, "读取请求数据长度异常");
        errorCodeMap.put(DTO_PARSE_ERROR, "Dto解析出错");
        errorCodeMap.put(DTO_VERSION_INCOMPATIBLE, "Dto版本不兼容");
        errorCodeMap.put(DONT_MACTH_DTO_TYPE, "没有匹配到Dto类型");
        errorCodeMap.put(CHUNK_TYPE_DONT_MACTH, "Chunk类型不匹配");

        //业务异常
        errorCodeMap.put(FLOW_ERROR, "流程调度异常");
        errorCodeMap.put(BASE_SERVICE_ERROR, "基础服务执行异常");
        errorCodeMap.put(INVAILD_ROUTE_RULE, "非法的路由信息");
        errorCodeMap.put(NO_TOKEN, "无可用令牌");

        errorCodeMap.put(READ_FILE_MSG_BEAN_ERROR, "读取FileMsgBean出错");
        errorCodeMap.put(DEL_SAME_FILE_FROM_OTHERS_ERROR, "删除其他节点同名文件失败");
        errorCodeMap.put(RESOURCE_NOT_ENOUGH, "资源不足");
        errorCodeMap.put(TASK_RESOURCE_NOT_ENOUGH, "Task资源不足");
        errorCodeMap.put(TOKEN_RESOURCE_NOT_ENOUGH, "Token资源不足");
        errorCodeMap.put(NODE_NOT_FOUND_ERROR, "没有所需节点");
        errorCodeMap.put(FILE_MD5_VALID_FAIL, "未校验文件MD5或校验失败");
        errorCodeMap.put(READ_DTO_TRY_COUNT_OUT_OF_LIMIT, "尝试读取Dto次数超出限制");

        errorCodeMap.put(CERTIFICATE_EXCEPTION, "安全证书异常");
        errorCodeMap.put(INTERRUPTED_EXCEPTION, "中断异常");
        errorCodeMap.put(J_RUNTIME_EXCEPTION, "运行时异常");

    }

    public static String getCodeMsg(String code) {
        String msg = errorCodeMap.get(code);
        if (msg == null) msg = code;
        else msg = code + "-" + msg;
        return msg;
    }

    public static String getErrCode(Throwable e) {
        if (e instanceof FtpException) return ((FtpException) e).getCode();
        if (e instanceof SocketException) return SOCKET_ERROR;
        if (e instanceof java.io.IOException) return IO_EXCEPTION;
        if (e instanceof RuntimeException) return J_RUNTIME_EXCEPTION;
        return FAIL;
    }
}
