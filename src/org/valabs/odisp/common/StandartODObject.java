package org.valabs.odisp.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
 * @version $Id: StandartODObject.java,v 1.14 2005/01/25 19:03:33 valeks Exp $
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
   * Regex ����� ����������� ���������. �� ��������� ���������������� ������ �������.
   */
  private String match;

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

  /**
   * �������� ����� ����������� ���������.
   * 
   * @param newMatch ����� �����
   */
  protected final void setMatch(final String newMatch) {
    match = newMatch;
  }

  /**
   * ������ � RegEx ��������� ���������� ������ ����������.
   * 
   * @return ������ � regex
   */
  public final String getMatch() {
    return match;
  }

  /**
   * ����������� ���������������� �������� ����.
   * 
   * @param newName ��� �������
   * @deprecated ���������� ������������ ������ �����������
   * @see StandartODObject(String, String, String, String)
   */
  public StandartODObject(final String name) {
    initialize(name, "An old style StandartODObject", "0.0.0", "(C) Dumb user");
  }
  
  /**
   * ����������� ���������������� �������� ����.
   * 
   * @param newName ��� �������
   * @param version ������ �������
   * @param copyright ���������
   * @param ai �������������� ����������
   */
  public StandartODObject(final String newName, final String fullName, final String version, final String copyright) {
    initialize(newName, fullName, version, copyright);
  }

  /**
   * @param newName
   */
  private void initialize(final String newName, final String newFullName, final String newVersion, final String newCopyright) {
    messages = new ArrayList();
    name = newName;
    version = newVersion;
    copyright = newCopyright;
    fullName = newFullName;
    match = newName;
    logger = Logger.getLogger(newName);
    logger.setLevel(java.util.logging.Level.ALL);
    handlers = new HashMap();
    registerHandlers();
  }

  /**
   * ������ � ����������.
   * 
   * @return ������ �� ���������
   */
  protected final Dispatcher getDispatcher() {
    return dispatcher;
  }

  /**
   * ���������� ���������� ODISP ��� �������.
   * 
   * @return ODISP ��� �������
   */
  public final String getObjectName() {
    return name;
  }

  /**
   * ������������� ���������� ��� �������� �������.
   * 
   * @param d ��������� ���������� � ���� ��������
   */
  public final void setDispatcher(final Dispatcher d) {
    this.dispatcher = d;
  }

  /**
   * ���������� ������� ����������.
   * 
   * @param cfg ����� �������
   */
  public void setConfiguration(final Map cfg) {
    configuration = cfg;
  }

  /**
   * �������� �������� ��������� ������������.
   * 
   * @param name ��� ���������
   */
  protected final String getParameter(final String name) {
    if (configuration != null && configuration.containsKey(name)) { return (String) configuration.get(name); }
    return null;
  }

  /**
   * �������� �������� ��������� ������������ � ������ �������� ��-���������.
   * 
   * @param name ��� ���������
   * @param defValue �������� �� ���������
   */
  protected final String getParameter(final String name, final String defValue) {
    return getParameter(name) == null ? defValue : getParameter(name);
  }

  /**
   * ����������� ����������� ���������.
   * 
   * @param message ���������� ��� ���������
   * @param handler ����������
   */
  protected final void addHandler(final String message, final MessageHandler handler) {
    if (handlers.containsKey(message)) { return; }
    handlers.put(message, handler);
  }

  /**
   * ��������� ���������.
   * 
   * @param msg ��������� ��� ���������
   */
  public void handleMessage(final Message msg) {
    if (ModuleAboutMessage.equals(msg)) {
      Message m = dispatcher.getNewMessage();
      ModuleAboutReplyMessage.setup(m, msg.getOrigin(), getObjectName(), msg
          .getId());
      ModuleAboutReplyMessage.setName(m, fullName);
      ModuleAboutReplyMessage.setVersion(m, version);
      ModuleAboutReplyMessage.setCopyright(m, copyright);
      dispatcher.send(m);
      return;
    } else if (ModuleStatusMessage.equals(msg)) {
      Message m = dispatcher.getNewMessage();
      ModuleStatusReplyMessage.setup(m, msg.getOrigin(), getObjectName(), msg.getId());
      objectStatus.setupStatusReply(m);
      dispatcher.send(m);
      return;
    }
    if (blockedState && !msg.isOOB()) {
      // ���������� ���� OOB ���������
      messages.add(msg);
      return;
    }
    boolean handled = false;

    if (SessionManager.getSessionManager().processMessage(msg)) {
      handled = true;
    }
    if (handlers.containsKey(msg.getAction())) {
      if (!handled) {
        ((MessageHandler) handlers.get(msg.getAction())).messageReceived(msg);
        handled = true;
      }
    }
    if (!handled) {
      logger.finer(" (" + getObjectName() + ") there is no handler for message " + msg.getAction());
    }
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
   * @param type ������� ������
   * @return ��� ��������
   */
  public int cleanUp(final int type) {
    return 0;
  }

  /**
   * ���������� ��������� ����������. � ���� ��������� ��� ���������, ������� ���� �������� �������� (�����
   * ��������� � ��������� "od_"), ����������� ��� ����������� ���������. ��� ����� ��������� ��
   * "�������������" ����������� ��������� ���������� �� ��������� ���������� Sender-������.
   * 
   * @param newState ����� ���������
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
          Iterator it = localMessages.iterator();
          while (it.hasNext()) {
            Message elt = (Message) it.next();
            handleMessage(elt);
          }
        }
      }.start();
    }
  }
  
  public Map exportState() {
    return null;
  }
  
  public void importState(Map oldState) {
    
  }

  	/** �������� ������� �������. */
  protected class ObjectStatus {
    public static final String NOERROR = "noerror";
    private List runningTasks = new ArrayList();
    private List completedTasks = new ArrayList();
    private List failedTasks = new ArrayList();
    private String runningState = ObjectStatus.NOERROR;
    
    public void taskStarted(String task) {
      runningTasks.add(task);
    }
    
    public void taskCompleted(String task) {
      runningTasks.remove(task);
      completedTasks.add(task);
    }
    
    public void taskFailed(String task) {
      runningTasks.remove(task);
      failedTasks.add(task);
    }
    
    public void setStatus(String newStatus) {
      runningState = newStatus;
    }
    
    public void setupStatusReply(Message m) {
      ModuleStatusReplyMessage.initAll(m, runningState, runningTasks, completedTasks, failedTasks);
    }
  }
} // StandartODObject
