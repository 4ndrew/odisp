package org.valabs.stdobj.bugtrack;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import org.valabs.odisp.common.ExceptionHandler;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;
import org.valabs.odisp.common.StandartODObject;
import org.valabs.stdmsg.BugSignalMessage;
import org.valabs.stdmsg.ModuleAboutMessage;
import org.valabs.stdmsg.ModuleAboutReplyMessage;
import org.valabs.stdmsg.ODObjectLoadedMessage;


/** Объект, который обеспечивает создание полного сообщения об ошибке возникшей во
 * время работы системы.
 * @author (C) 2005 <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>
 * @version $Id: BugTrack.java,v 1.1 2005/01/11 20:37:59 valeks Exp $
 */
public class BugTrack extends StandartODObject implements ExceptionHandler, MessageHandler {
  public static final String NAME = "bugtrack";
  
  public static final String FULL_NAME = "about.bugtrack";
  
  public static final String VERSION = "0.1.0";
  
  public static final String COPYRIGHT = "(C) 2005 Valentin A. Alekseev";
  
  private BugWriter bw;
  
  public BugTrack(Integer id) {
    super(NAME + id);
  }

  public void registerHandlers() {
    addHandler(ODObjectLoadedMessage.NAME, this);
    addHandler(BugSignalMessage.NAME, this);
    addHandler(ModuleAboutMessage.NAME, new MessageHandler() {
      public void messageReceived(Message msg) {
        Message m = dispatcher.getNewMessage();
        ModuleAboutReplyMessage.setup(m, msg.getOrigin(), getObjectName(), msg.getId());
        ModuleAboutReplyMessage.setName(m, FULL_NAME);
        ModuleAboutReplyMessage.setVersion(m, VERSION);
        ModuleAboutReplyMessage.setCopyright(m, COPYRIGHT);
        dispatcher.send(m);
      }
    });
  }
  
  public void messageReceived(final Message msg) {
    if (ODObjectLoadedMessage.equals(msg)) {
      dispatcher.addExceptionHandler(this);
    } else if (BugSignalMessage.equals(msg)) {
      String id = BugSignalMessage.getBugId(msg);
      String pc = BugSignalMessage.getBugPC(msg);
      String ai = BugSignalMessage.getBugAI(msg);
      createBugReport(id, pc, ai);
    }
  }
  
  public void setConfiguration(Map cfg) {
    super.setConfiguration(cfg);
    bw = new BugWriter_Plain(cfg);
  }
  
  public void signalException(Exception e) {
    StringWriter sw = new StringWriter();
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
        } catch (InterruptedException e) {
          
        }
        ss.stopSnapshot();
        logger.warning("Snapshot taken.");
        bw.writeBugReport(id, pc, ai, ss);
      }
    }.start();
  }

  public String[] getDepends() {
    String[] deps = { "dispatcher" };
    return deps;
  }

  public String[] getProviding() {
    String[] providing = { NAME };
    return providing;
  }
}
