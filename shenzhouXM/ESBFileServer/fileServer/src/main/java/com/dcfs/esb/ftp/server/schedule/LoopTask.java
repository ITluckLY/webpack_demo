package com.dcfs.esb.ftp.server.schedule;

import java.util.Map;

public interface LoopTask {
    void execute() throws Exception;//NOSONAR

    void init(Map<String, String> params);
}
