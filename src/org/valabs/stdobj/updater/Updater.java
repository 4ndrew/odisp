package org.valabs.stdobj.updater;

import java.util.Timer;
import java.util.TimerTask;

import org.doomdark.uuid.UUID;
import org.valabs.odisp.Launcher;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;
import org.valabs.odisp.common.StandartODObject;
import org.valabs.stdmsg.ODObjectLoadedMessage;


/** Компонент поддержки обновлений.
 * @author <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>
 * @version $Id: Updater.java,v 1.1 2005/01/27 14:17:41 valeks Exp $
 */
public class Updater extends StandartODObject implements MessageHandler {
  private static final String NAME = "updater";
  private static final String FULLNAME = "Update support";
  private static final String VERSION = "0.1.0";
  private static final String COPYRIGHT = "(C) 2005 Valentin A. Alekseev";
  
  private boolean checkForUpdate() {
    String oldChecksum = System.getProperty("odisp.libchecksum");
    String libdirs = System.getProperty("odisp.libdirs");
    return !Launcher.folderChecksum(libdirs).equals(oldChecksum);
  }
  
  /**
   * @param name
   */
  public Updater() {
    super(NAME, FULLNAME, VERSION, COPYRIGHT);
  }

  public void registerHandlers() {
    addHandler("test", this);
    addHandler(ODObjectLoadedMessage.NAME, this);
  }
  
  /* (non-Javadoc)
   * @see org.valabs.odisp.common.MessageHandler#messageReceived(org.valabs.odisp.common.Message)
   */
  public void messageReceived(Message msg) {
    if (ODObjectLoadedMessage.equals(msg)) {
      if (getParameter("auto", "yes").equals("yes")) {
        new Timer(true).schedule(new TimerTask() {
          public void run() {
            if (checkForUpdate()) {
              logger.info("Updates avaliable. Restarting");
              Message m = dispatcher.getNewMessage("od_shutdown", "dispatcher", getObjectName(), UUID.getNullUUID());
              m.addField("exitcode", new Integer(-1));
              m.setCorrect(true);
              dispatcher.send(m);
            }
          } 
        }, 0, 500);
      }
    } else if (msg.getAction().equals("test")) {
      logger.info("Checking for components update...");
      if (checkForUpdate()) {
        logger.info("Updates avaliable. Restarting");
        Message m = dispatcher.getNewMessage("od_shutdown", "dispatcher", getObjectName(), msg.getId());
        m.addField("exitcode", new Integer(-1));
        m.setCorrect(true);
        dispatcher.send(m);
      }
    }
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.ODObject#getDepends()
   */
  public String[] getDepends() {
    String[] depends = { "dispatcher", };
    return depends;
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.ODObject#getProviding()
   */
  public String[] getProviding() {
    String[] providing = { NAME, };
    return providing;
  }

}
