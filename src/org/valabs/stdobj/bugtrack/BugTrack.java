package org.valabs.stdobj.bugtrack;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import org.valabs.odisp.common.ExceptionHandler;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;
import org.valabs.odisp.common.StandartODObject;
import org.valabs.stdmsg.BugSignalMessage;
import org.valabs.stdmsg.ODObjectLoadedMessage;


/** Объект, который обеспечивает создание полного сообщения об ошибке возникшей во
 * время работы системы.
 * @author (C) 2005 <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>
 * @version $Id: BugTrack.java,v 1.6 2005/07/22 13:06:56 dron Exp $
 */
public class BugTrack extends StandartODObject implements ExceptionHandler, MessageHandler {
  public static final String NAME = "bugtrack";
  
  public static final String FULL_NAME = "Bug report service";
  
  public static final String VERSION = "0.1.0";
  
  public static final String COPYRIGHT = "(C) 2005 Valentin A. Alekseev";
  
  private AbstractBugWriter bw;
  
  public BugTrack() {
    super(NAME, FULL_NAME, VERSION, COPYRIGHT);
  }

  public void registerHandlers() {
    addHandler(ODObjectLoadedMessage.NAME, this);
    addHandler(BugSignalMessage.NAME, this);
  }
  
  public void messageReceived(final Message msg) {
    if (ODObjectLoadedMessage.equals(msg)) {
      dispatcher.addExceptionHandler(this);
    } else if (BugSignalMessage.equals(msg)) {
      final String id = BugSignalMessage.getBugId(msg);
      final String pc = BugSignalMessage.getBugPC(msg);
      final String ai = BugSignalMessage.getBugAI(msg);
      createBugReport(id, pc, ai);
    }
  }
  
  public void setConfiguration(final Map cfg) {
    super.setConfiguration(cfg);
    bw = new BugWriter_Plain(cfg);
  }
  
  public void signalException(final Exception e) {
    final StringWriter sw = new StringWriter();
    e.printStackTrace(new PrintWriter(sw));
    createBugReport("EX-0", e.toString(), sw.toString());
  }

  private void createBugReport(final String id, final String pc, final String ai) {
    final SystemSnapshot ss = new SystemSnapshot(this, dispatcher);
    logger.warning("Bug signaled. Taking system snapshot.");
    new Thread() {
      public void run() {
        ss.startSnapshot();
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) { /* игнорируется */ }
        ss.stopSnapshot();
        logger.warning("Snapshot taken.");
        bw.writeBugReport(id, pc, ai, ss);
      }
    }.start();
  }

  public String[] getDepends() {
    final String[] deps = { "dispatcher" };
    return deps;
  }

  public String[] getProviding() {
    final String[] providing = { NAME };
    return providing;
  }
}
