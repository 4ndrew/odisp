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
package org.valabs.odisp.standart5;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.ODISPMessageHandler;
import org.valabs.odisp.common.StandartODObject5;
import org.valabs.stdmsg.CopyrightGetMessage;
import org.valabs.stdmsg.CopyrightGetReplyMessage;
import org.valabs.stdmsg.ModuleStatusMessage;
import org.valabs.stdmsg.ModuleStatusReplyMessage;
import org.valabs.stdmsg.ODShutdownMessage;

/** ���������� ��������� ��������� ����������.
 * @author (C) <a href="mailto:valeks@valabs.spb.ru">�������� �������� �.</a>, 2005
 * @version $Id: DispatcherHandler5.java,v 1.3 2006/03/29 11:33:24 valeks Exp $
 */
public class DispatcherHandler5 extends StandartODObject5 {

  private static final String NAME = "stddispatcher5";
  private static final String FULLNAME = "ODISP Dispatcher Core for Java 1.5";
  private static final String VERSION = "0.1.0";
  private static final String COPYRIGHT = "(C) Valentin A. Alekseev, 2005";
  /** �������� ��������. */
  private ObjectManager5 oman;
  /** �������� ��������. */
  private ResourceManager5 rman;
  
  private Thread runThread;
  
  public DispatcherHandler5() {
    super(NAME, FULLNAME, VERSION, COPYRIGHT, DispatcherHandler5.class);
    setInstance(this);
  }
  
  @ODISPMessageHandler(ModuleStatusMessage.NAME)
  public void handleModuleStatus(final Message msg) {
    final Message m = dispatcher.getNewMessage();
    ModuleStatusReplyMessage.setup(m, msg.getOrigin(),
            getObjectName(), msg.getId());
    String runningState = "No error";
    final List<String> running = new ArrayList<String>();
    final List<String> failed = new ArrayList<String>();
    
    for (ObjectEntry oe: oman.getObjects().values()) {
        if (oe.isLoaded()) {
            running.add(oe.getObject().getObjectName());
        } else {
            failed.add(oe.getObject().getObjectName() + " not loaded. Dependencies not met: " + oe.getDepends());
            runningState = "Warning. Not all pre-requested objects are loaded. Some dependencies aren't met.";
        }
    }
    final List<String> resStat = rman.statRequest();
    for (String line: resStat) {
        running.add(line);
    }
    ModuleStatusReplyMessage.setRunningState(m, runningState);
    ModuleStatusReplyMessage.setRunningTasks(m, running);
    ModuleStatusReplyMessage.setCompletedTasks(m, new ArrayList());
    ModuleStatusReplyMessage.setFailedTasks(m, failed);
    dispatcher.send(m);
  }
  
  @ODISPMessageHandler(CopyrightGetMessage.NAME)
  public void handleCopyrightMessage(final Message msg) {
    List<String> result = new ArrayList<String>(2);
    result.add("ODISP MOM (C) 2003-2005 Valentin A. Alekseev <valeks@valabs.spb.ru>, Andrew A. Porohin <@>");
    result.add("Java UUID Generator (JUG) Copyright (c) 2002-2005 Tatu Saloranta, tatu.saloranta@iki.fi");
    Message m = dispatcher.getNewMessage();
    CopyrightGetReplyMessage.setup(m, msg.getOrigin(), getObjectName(), msg.getId());
    CopyrightGetReplyMessage.setCopyrights(m, result);
    dispatcher.send(m);
  }
  
  @ODISPMessageHandler(value=ODShutdownMessage.NAME, mapping={ODShutdownMessage.idxEXITCODE})
  public void handleODShutdown(final Message msg, Integer exitCode) {
    logger.info(toString() + " shutting down...");
    // ��������
    oman.unloadObject(getObjectName(), (exitCode == null ? 0 : exitCode.intValue()));
    runThread.interrupt();   
  }
  
  /**
   * @see org.valabs.odisp.common.ODObject#getDepends()
   */
  public String[] getDepends() {
    return new String [] {};
  }

  /**
   * @see org.valabs.odisp.common.ODObject#getProviding()
   */
  public String[] getProviding() {
    return new String[] {
        NAME,
        "dispatcher",
    };
  }
  
  @Override
  public void setConfiguration(Map cfg) {
    runThread = (Thread) cfg.get("runthr");
    cfg.remove("runthr");
    // ������ ������ �� ����, ��� DispatcherHandler ����������� ������ ��� ODISP5.
    oman = (ObjectManager5) dispatcher.getObjectManager();
    rman = (ResourceManager5) dispatcher.getResourceManager();
    super.setConfiguration(cfg);
  }
}
