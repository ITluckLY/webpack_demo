package com.dcfs.esb.ftp.process;

import com.dcfs.esb.ftp.common.helper.CapabilityDebugHelper;
import com.dcfs.esb.ftp.process.handler.interfac.ProcessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 每个channel对应一个流程执行，中间过程不变
 * Created by mocg on 2017/6/5.
 */
public class ProcessExecutor {
  private static final Logger log = LoggerFactory.getLogger(ProcessHandler.class);

  private List<ProcessHandler> handlers = new ArrayList<>();
  private boolean doneFinish = false;
  private boolean processThrowable = false;

  public void start(ProcessHandlerContext cxt) throws Exception {//NOSONAR
    cxt.setProcessStatus(ProcessStatus.START);
    for (ProcessHandler handler : handlers) {
      if (!cxt.isContinueNextPreProcess()) break;
      if (CapabilityDebugHelper.isDebug()) {
        CapabilityDebugHelper.markCurrTime("ProcessExecutor-handler.start-before#" + handler.getClass().getSimpleName());
      }

      handler.start(cxt);

      if (CapabilityDebugHelper.isDebug()) {
        CapabilityDebugHelper.markCurrTime("ProcessExecutor-handler.start-end#" + handler.getClass().getSimpleName());
      }
    }
  }

  public void invoke(ProcessHandlerContext cxt, Object obj) throws Exception {//NOSONAR
    try {
      cxt.setProcessStatus(ProcessStatus.PRE_PROCESS);
      for (ProcessHandler handler : handlers) {
        if (!cxt.isContinueNextPreProcess()) break;
        if (CapabilityDebugHelper.isDebug()) {
          CapabilityDebugHelper.markCurrTime("ProcessExecutor-handler.preProcess-before#" + handler.getClass().getSimpleName());
        }

        handler.preProcess(cxt);

        if (CapabilityDebugHelper.isDebug()) {
          CapabilityDebugHelper.markCurrTime("ProcessExecutor-handler.preProcess-end#" + handler.getClass().getSimpleName());
        }
      }
      cxt.setProcessStatus(ProcessStatus.PROCESS);
      Object nextObj = obj;
      for (ProcessHandler handler : handlers) {
        if (!cxt.isContinueNextProcess()) break;
        if (CapabilityDebugHelper.isDebug()) {
          CapabilityDebugHelper.markCurrTime("ProcessExecutor-handler.process-before#" + handler.getClass().getSimpleName());
        }

        nextObj = handler.process(cxt, nextObj);

        if (CapabilityDebugHelper.isDebug()) {
          CapabilityDebugHelper.markCurrTime("ProcessExecutor-handler.process-end#" + handler.getClass().getSimpleName());
        }
      }
      cxt.setProcessStatus(ProcessStatus.AFTER_PROCESS);
      for (ProcessHandler handler : handlers) {
        if (!cxt.isContinueNextAfterProcess()) break;
        if (CapabilityDebugHelper.isDebug()) {
          CapabilityDebugHelper.markCurrTime("ProcessExecutor-handler.afterProcess-before#" + handler.getClass().getSimpleName());
        }

        handler.afterProcess(cxt);

        if (CapabilityDebugHelper.isDebug()) {
          CapabilityDebugHelper.markCurrTime("ProcessExecutor-handler.afterProcess-end#" + handler.getClass().getSimpleName());
        }
      }

      if (cxt.isFinish() && !doneFinish) {
        doneFinish = true;
        cxt.setProcessStatus(ProcessStatus.FINISH);
        for (ProcessHandler handler : handlers) {
          if (!cxt.isContinueNextFinish()) break;
          if (CapabilityDebugHelper.isDebug()) {
            CapabilityDebugHelper.markCurrTime("ProcessExecutor-handler.finish-before#" + handler.getClass().getSimpleName());
          }

          handler.finish(cxt);

          if (CapabilityDebugHelper.isDebug()) {
            CapabilityDebugHelper.markCurrTime("ProcessExecutor-handler.finish-end#" + handler.getClass().getSimpleName());
          }
        }
        for (ProcessHandler handler : handlers) {
          if (!cxt.isContinueNextFinish2()) break;
          if (CapabilityDebugHelper.isDebug()) {
            CapabilityDebugHelper.markCurrTime("ProcessExecutor-handler.finish2-before#" + handler.getClass().getSimpleName());
          }

          handler.finish2(cxt);

          if (CapabilityDebugHelper.isDebug()) {
            CapabilityDebugHelper.markCurrTime("ProcessExecutor-handler.finish2-end#" + handler.getClass().getSimpleName());
          }
        }
      }
    } catch (Throwable e) {//NOSONAR
      processThrowable = true;
      log.error("ProcessExecutor.invoke err", e);
      cxt.setProcessStatus(ProcessStatus.EXCEPTION_CAUGHT);
      for (ProcessHandler handler : handlers) {
        if (!cxt.isContinueNextExceptionCaught()) break;
        //todo 告诉其他handler前面的异常处理再异常
        try {
          handler.exceptionCaught(cxt, e);
        } catch (Throwable e1) {//NOSONAR
          log.error("流程异常处理再异常", e1);
        }
      }
      throw e;
    } finally {
      if (processThrowable || cxt.isFinish()) {
        clean(cxt);
      }
    }
    cxt.setProcessStatus(ProcessStatus.ENDED);
  }

  public void clean(ProcessHandlerContext cxt) {
    if (handlers != null && cxt != null) {
      cxt.setProcessStatus(ProcessStatus.CLEAN);
      for (ProcessHandler handler : handlers) {
        if (CapabilityDebugHelper.isDebug()) {
          CapabilityDebugHelper.markCurrTime("ProcessExecutor-handler.clean-before#" + handler.getClass().getSimpleName());
        }
        try {
          handler.clean(cxt);
        } catch (Throwable e1) {//NOSONAR
          log.error("释放ProcessHandler资源出错", e1);
        }
        if (CapabilityDebugHelper.isDebug()) {
          CapabilityDebugHelper.markCurrTime("ProcessExecutor-handler.clean-end#" + handler.getClass().getSimpleName());
        }
      }
      handlers.clear();
      handlers = null;
    } else if (handlers != null) {
      handlers.clear();
      handlers = null;
    }
  }

  public List<ProcessHandler> getHandlers() {
    return handlers;
  }

}
