package org.valabs.stdobj.bugtrack;

import java.util.ArrayList;
import java.util.List;

import org.doomdark.uuid.UUID;
import org.valabs.odisp.SessionManager;
import org.valabs.odisp.common.Dispatcher;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;
import org.valabs.stdmsg.ModuleAboutMessage;
import org.valabs.stdmsg.ModuleAboutReplyMessage;
import org.valabs.stdmsg.ModuleStatusMessage;
import org.valabs.stdmsg.ModuleStatusReplyMessage;

/** Класс описывающий мгновенный слепок системы.
 * На данный момент сохраняется список и состояние модулей.
 * @author (C) 2005 <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>
 * @version $Id: SystemSnapshot.java,v 1.1 2005/01/11 20:37:59 valeks Exp $
 */
class SystemSnapshot implements MessageHandler {
  private BugTrack track;
  private List aboutList = new ArrayList();
  private List moduleList = new ArrayList();
  private UUID maUUID;
  private Dispatcher dispatcher;
  
  public SystemSnapshot(BugTrack track, Dispatcher _dispatcher) {
    dispatcher = _dispatcher;
    this.track = track;
  }
  
  	public void startSnapshot() {
  	  Message m = dispatcher.getNewMessage();
  	  ModuleAboutMessage.setup(m, ".*", track.getObjectName(), UUID.getNullUUID());
  	  maUUID = m.getId();
  	  SessionManager.getSessionManager().addMessageListener(maUUID, this, true);
  	  dispatcher.send(m);
  	}

  	public void messageReceived(Message msg) {
  	  if (ModuleAboutReplyMessage.equals(msg)) {
  	    aboutList.add(new ModuleAbout(msg));
  	    Message m = dispatcher.getNewMessage();
  	    ModuleStatusMessage.setup(m, msg.getOrigin(), track.getObjectName(), msg.getId());
  	    SessionManager.getSessionManager().addMessageListener(m.getId(), this);
  	    dispatcher.send(m);
  	  } else if (ModuleStatusReplyMessage.equals(msg)) {
  	    moduleList.add(new ModuleStatus(msg));
  	  }
  	}
  	
  	public void stopSnapshot() {
  	  SessionManager.getSessionManager().removeMessageListener(maUUID, this);
  	}
  	
  	
  	
  public List getModuleAboutList() {
    return aboutList;
  }
  public List getModuleStatusList() {
    return moduleList;
  }
  
  public class ModuleAbout {
    private Message orig;
    public ModuleAbout(Message _orig) {
      orig = _orig;
    }
    
    public String toString() {
      String res = "bugtrack.module: " + ModuleAboutReplyMessage.getName(orig)
      		+ " bugtrack.version " + ModuleAboutReplyMessage.getVersion(orig)
      		+ " bugtrack.copyright " + ModuleAboutReplyMessage.getCopyright(orig)
      		+ " bugtrack.ai " + ModuleAboutReplyMessage.getAI(orig);
      return res;
    }
    
    public String getName() {
      return ModuleAboutReplyMessage.getName(orig);
    }
    public String getVersion() {
      return ModuleAboutReplyMessage.getVersion(orig);
    }
    public String getCopyright() {
      return ModuleAboutReplyMessage.getCopyright(orig);
    }
    public String getAI() {
      return ModuleAboutReplyMessage.getAI(orig);
    }
  }
  
  public class ModuleStatus {
    private Message orig;
    public ModuleStatus(Message _orig) {
      orig = _orig;
    }
    
    public String toString() {
      String res = "bugtrack.module: " + orig.getOrigin() 
      	+ " bugtrack.runningstate " + ModuleStatusReplyMessage.getRunningState(orig)
      	+ " bugtrack.runningtasks " + ModuleStatusReplyMessage.getRunningTasks(orig)
      	+ " bugtrack.completedtasks " + ModuleStatusReplyMessage.getCompletedTasks(orig)
      	+ " bugtrack.failedtasks " + ModuleStatusReplyMessage.getFailedTasks(orig);
      return res;
    }
    
    public String getName() {
      return orig.getOrigin();
    }
    
    public String getRunningState() {
      return ModuleStatusReplyMessage.getRunningState(orig);
    }

    public List getRunningTasks() {
      return ModuleStatusReplyMessage.getRunningTasks(orig);
    }

    public List getCompletedTasks() {
      return ModuleStatusReplyMessage.getCompletedTasks(orig);
    }
    
    public List getFailedTasks() {
      return ModuleStatusReplyMessage.getFailedTasks(orig);
    }
    
  }
}