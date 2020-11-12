package com.dcfs.esc.ftp.comm.constant;

/**
 * Created by mocg on 2017/6/7.
 */
public final class SysCfg {
    private SysCfg() {
    }

    private static boolean printDtoJson = false;
    private static int pieceNum = SysConst.DEF_PIECE_NUM;

    //getset

    public static boolean isPrintDtoJson() {
        return printDtoJson;
    }

    public static void setPrintDtoJson(boolean printDtoJson) {
        SysCfg.printDtoJson = printDtoJson;
    }

    public static int getPieceNum() {
        return pieceNum;
    }

    public static void setPieceNum(int pieceNum) {
        SysCfg.pieceNum = pieceNum;
    }
}
