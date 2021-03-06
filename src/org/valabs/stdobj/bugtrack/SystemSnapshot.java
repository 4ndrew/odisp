/* ODISP -- Message Oriented Middleware
 * Copyright (C) 2003-2005 Valentin A. Alekseev
 * Copyright (C) 2003-2005 Andrew A. Porohin 
 * 
 * ODISP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.1 of the License.
 * 
 * ODISP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ODISP.  If not, see <http://www.gnu.org/licenses/>.
 */
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

/** ����� ����������� ���������� ������ �������.
 * �� ������ ������ ����������� ������ � ��������� �������.
 * @author (C) 2005 <a href="mailto:valeks@valabs.spb.ru">�������� �������� �.</a>
 * @version $Id: SystemSnapshot.java,v 1.2 2005/02/27 12:37:30 valeks Exp $
 */
class SystemSnapshot implements MessageHandler {
  private final BugTrack track;
  private final List aboutList = new ArrayList();
  private final List moduleList = new ArrayList();
  private UUID maUUID;
  private final Dispatcher dispatcher;
  
  public SystemSnapshot(BugTrack track, Dispatcher _dispatcher) {
    dispatcher = _dispatcher;
    this.track = track;
  }
  
  	public void startSnapshot() {
  	  final Message m = dispatcher.getNewMessage();
  	  ModuleAboutMessage.setup(m, ".*", track.getObjectName(), UUID.getNullUUID());
  	  maUUID = m.getId();
  	  SessionManager.getSessionManager().addMessageListener(maUUID, this, true);
  	  dispatcher.send(m);
  	}

  	public void messageReceived(final Message msg) {
  	  if (ModuleAboutReplyMessage.equals(msg)) {
  	    aboutList.add(new ModuleAbout(msg));
  	    final Message m = dispatcher.getNewMessage();
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