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
package org.valabs.odisp.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.valabs.odisp.SessionManager;
import org.valabs.stdmsg.ModuleAboutMessage;
import org.valabs.stdmsg.ModuleAboutReplyMessage;
import org.valabs.stdmsg.ModuleStatusMessage;
import org.valabs.stdmsg.ModuleStatusReplyMessage;

/**
 * ����������� ������ ODISP.
 * 
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev </a>
 * @version $Id: StandartODObject.java,v 1.20 2005/09/28 13:29:49 valeks Exp $
 */

public abstract class StandartODObject implements ODObject {

  /** ������. */
  protected Logger logger;

  /** ��������� ���������� � ���� ��������. */
  protected Dispatcher dispatcher;

  /** ������ ��������� � ���������. */
  private List messages;

  /** ������� ���������������� ����������. */
  private Map configuration;

  /**
   * ���� �� ������������� ������������ ��� ���������.
   */
  private boolean matchAll = false;

  /** ���������� ��� ������� � ���� ODISP. */
  private String name;
  
  private String version;
  
  private String copyright;
  
  protected ObjectStatus objectStatus = new ObjectStatus();

  /** ����� ������������ ���������. */
  private Map handlers;

  /** ������� ���������� �������. */
  private boolean blockedState = false;

  private String fullName;
  
  private final Set providing = new HashSet();

  /**
   * ������������ �� ��� ���������.
   * @param newMatch true -- ��������� ��� ���������, false -- 
   * ��������� ������ ���� ���������
   */
  protected final void setMatchAll(final boolean newMatch) {
    matchAll = newMatch;
  }

  /**
   * �������������� �� ��� ���������.
   * @return true -- ��������� ��� ���������, false -- 
   * ��������� ������ ���� ���������
   */
  public final boolean getMatchAll() {
    return matchAll;
  }

  /**
   * ����������� ���������������� �������� ����.
   * 
   * @param name ��� �������.
   * @deprecated ���������� ������������ ������ �����������.
   * @see #StandartODObject(String, String, String, String)
   */
  public StandartODObject(final String name) {
    initialize(name, "An old style StandartODObject", "0.0.0", "(C) Dumb user");
  }
  
  /**
   * ����������� ���������������� �������� ����.
   * 
   * @param newName ��� �������.
   * @param fullName ������ ��� �������.
   * @param version ������ �������.
   * @param copyright ���������.
   */
  public StandartODObject(final String newName, final String fullName, final String version, final String copyright) {
    initialize(newName, fullName, version, copyright);
  }

  /**
   * ������������� �������.
   * 
   * @param newName ���.
   * @param newFullName ���ף������ ��������.
   * @param newVersion ������.
   * @param newCopyright �����.
   */
  private void initialize(final String newName, final String newFullName, final String newVersion, final String newCopyright) {
    messages = new ArrayList();
    name = newName;
    version = newVersion;
    copyright = newCopyright;
    fullName = newFullName;
    logger = Logger.getLogger(newName);
    logger.setLevel(java.util.logging.Level.ALL);
    handlers = new HashMap();
    registerHandlers();
    providing.addAll(Arrays.asList(getProviding()));
  }

  /**
   * ������ � ����������.
   * 
   * @return ������ �� ���������.
   */
  protected final Dispatcher getDispatcher() {
    return dispatcher;
  }

  /**
   * ���������� ���������� ODISP ��� �������.
   * 
   * @return ODISP ��� �������.
   */
  public final String getObjectName() {
    return name;
  }

  /**
   * ������������� ���������� ��� �������� �������.
   * 
   * @param newDisp ��������� ���������� � ���� ��������.
   */
  public final void setDispatcher(final Dispatcher newDisp) {
    this.dispatcher = newDisp;
  }

  /**
   * ���������� ������� ����������.
   * 
   * @param cfg ����� �������.
   */
  public void setConfiguration(final Map cfg) {
    configuration = cfg;
  }

  /**
   * �������� �������� ��������� ������������.
   * 
   * @param paramName ��� ���������.
   * @return �������� ��������� ��� null ���� �� �� ������
   */
  protected final String getParameter(final String paramName) {
    if (configuration != null && configuration.containsKey(paramName)) { return (String) configuration.get(paramName); }
    return null;
  }

  /**
   * �������� �������� ��������� ������������ � ������ �������� ��-���������.
   * 
   * @param paramName ��� ���������.
   * @param defValue �������� �� ���������.
   * @return �������� ��������� ���� �� ���������� ��� �������� �� defValue
   */
  protected final String getParameter(final String paramName, final String defValue) {
    return getParameter(paramName) == null ? defValue : getParameter(paramName);
  }

  /**
   * ����������� ����������� ���������.
   * 
   * @param message ���������� ��� ���������.
   * @param handler ����������.
   */
  protected final void addHandler(final String message, final MessageHandler handler) {
    if (handlers.containsKey(message)) { return; }
    handlers.put(message, handler);
  }

  /**
   * ��������� ���������.
   * �������� ��� ������ ���������:
   * <ul>
   * <li>��������� �������;
   * <li>��������� ��������� ������� ������� ��� ������� � ������ ���� ����� matchAll;
   * <li>��������� ����������� � ������� � ������;
   * </ul> 
   * 
   * @param msg ��������� ��� ���������.
   */
  public final void handleMessage0(final Message msg) {
    if (blockedState && !msg.isOOB()) {
      // ���������� ���� OOB ���������
      messages.add(msg);
      return;
    }
    
    if (isOur(msg)) {
      if (!handleMessageInternal(msg)) {
        handleMessageByObject(msg);
      }
    } else if (matchAll) {
      handleMessageByObject(msg);
    }
  }
  
  /**
   * �������� �������� �� ��������� ����� (����� ���������� - ������ ���
   * ��� �������).
   * 
   * @return true, ���� ��� ����, false - � ������ ������.
   */
  private boolean isOur(final Message msg) {
    boolean result = false;
    if (msg.getDestination().equals(name)) {
      result = true;
    } else if (providing.contains(msg.getDestination())) {
      result = true;
    }
    return result;
  }
  
  /**
   * ��������� ��������� ��������.
   * 
   * @param msg ��������� ��� ���������. 
   */
  private void handleMessageByObject(final Message msg) {
    boolean handled = false;

    if (isOur(msg) && SessionManager.getSessionManager().processMessage(msg)) {
      handled = true;
    }
    if (isOur(msg) && handlers.containsKey(msg.getAction())) {
      if (!handled) {
        ((MessageHandler) handlers.get(msg.getAction())).messageReceived(msg);
        handled = true;
      }
    }
        
    if (!handled) {
      handleMessage(msg);
    }
  }

  /**
   * ��������� ����������� ���������.
   * 
   * @param msg C��������.
   */
  private boolean handleMessageInternal(final Message msg) {
    boolean result = false;
    if (ModuleAboutMessage.equals(msg)) {
      final Message m = dispatcher.getNewMessage();
      ModuleAboutReplyMessage.setup(m, msg.getOrigin(), name, msg.getId());
      ModuleAboutReplyMessage.setName(m, fullName);
      ModuleAboutReplyMessage.setVersion(m, version);
      ModuleAboutReplyMessage.setCopyright(m, copyright);
      dispatcher.send(m);
      result = true;
    } else if (ModuleStatusMessage.equals(msg)) {
      final Message m = dispatcher.getNewMessage();
      ModuleStatusReplyMessage.setup(m, msg.getOrigin(), name, msg.getId());
      objectStatus.setupStatusReply(m);
      dispatcher.send(m);
      result = true;
    }
    return result;
  }

  /**
   * ���������� ��-���������.
   * @param msg C��������.
   */
  public void handleMessage(final Message msg) {
    logger.fine("There is no handler for message " + msg.getAction() + " in " + name);
  }

  /**
   * ����������� ������ ������ ����������� ���� ����� ��� ����������� ������������.
   */
  protected void registerHandlers() {
    /* DO NOTHING BY DEFAULT */
  }

  /**
   * ����� ���������� ��� ������� ������ ������.
   * 
   * @param type ������� ������.
   * @return ��� ��������.
   */
  public int cleanUp(final int type) {
    return 0;
  }

  /**
   * ���������� ��������� ����������. � ���� ��������� ��� ���������, ������� ���� �������� �������� (�����
   * ��������� � ������������� isOOB), ����������� ��� ����������� ���������. ��� ����� ��������� ��
   * "�������������" ����������� ��������� ���������� �� ��������� ���������� Sender-������.
   * 
   * @param newState ����� ���������.
   * @see Message#isOOB()
   * @see Message#setOOB(boolean)
   */
  protected void setBlockedState(final boolean newState) {
    blockedState = newState;
    if (!blockedState) {
      new Thread("sender-message-flush") {

        List localMessages;

        public void run() {
          synchronized (messages) {
            localMessages = new ArrayList(messages);
            messages.clear();
          }
          final Iterator msgIt = localMessages.iterator();
          while (msgIt.hasNext()) {
            handleMessage((Message) msgIt.next());
          }
        }
      }.start();
    }
  }
  
  /**
   * ���������� �������� ��������� ������� ��� ������������ �������������� ��� ������������.
   * �������, ������� ������������ ������������ ���������� ����������� ������ ����� ���
   * ���������� ������������� ������.
   *   
   * @return ����� ������ �������. ��� �����, ��� ������ Map <b>������ ����</b> ������ �������������
   * ��������� {@link java.io.Serializable}.
   */
  public Map exportState() {
    return null;
  }
  
  /**
   * �������������� ������ ������� ��� ������������.
   * 
   * @param oldState �����Σ���� ������ �������. 
   */
  public void importState(final Map oldState) {
    /* ������ �� ������ ��-���������. */
  }

  /** �������� ������� �������. */
  protected static class ObjectStatus {
    public static final String NOERROR = "noerror";
    private List runningTasks;
    private List completedTasks;
    private List failedTasks;
    private String runningState = ObjectStatus.NOERROR;
    
    public void taskStarted(final String task) {
      if (runningTasks == null) {
        runningTasks = new ArrayList();
      }
      runningTasks.add(task);
    }
    
    public void taskCompleted(final String task) {
      if (completedTasks == null) {
        completedTasks = new ArrayList();
      }
      if (runningTasks == null) {
        runningTasks = new ArrayList();
      }

      runningTasks.remove(task);
      completedTasks.add(task);
    }
    
    public void taskFailed(final String task) {
      if (failedTasks == null) {
        failedTasks = new ArrayList();
      }
      if (runningTasks == null) {
        runningTasks = new ArrayList();
      }

      runningTasks.remove(task);
      failedTasks.add(task);
    }
    
    public void setStatus(final String newStatus) {
      runningState = newStatus;
    }
    
    public void setupStatusReply(final Message message) {
      ModuleStatusReplyMessage.initAll(message, runningState, runningTasks, completedTasks, failedTasks);
    }
  }
} // StandartODObject
