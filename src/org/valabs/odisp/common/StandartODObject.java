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
 * Стандартный объект ODISP.
 * 
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev </a>
 * @version $Id: StandartODObject.java,v 1.16 2005/02/27 12:37:29 valeks Exp $
 */

public abstract class StandartODObject implements ODObject {

  /** Журнал. */
  protected Logger logger;

  /** Диспетчер работающий с этим объектом. */
  protected Dispatcher dispatcher;

  /** Список сообщений к обработке. */
  private List messages;

  /** Таблица конфигурационных параметров. */
  private Map configuration;

  /**
   * Есть ли необходимость обрабатывать все сообщения.
   */
  private boolean matchAll = false;

  /** Внутреннее имя объекта в ядре ODISP. */
  private String name;
  
  private String version;
  
  private String copyright;
  
  protected ObjectStatus objectStatus = new ObjectStatus();

  /** Карта обработчиков сообщений. */
  private Map handlers;

  /** Признак блокировки объекта. */
  private boolean blockedState = false;

  private String fullName;

  /**
   * Обрабатывать ли все сообщения.
   */
  protected final void setMatchAll(final boolean newMatch) {
    matchAll = newMatch;
  }

  /**
   * Обрабатываются ли все сообщения.
   */
  public final boolean getMatchAll() {
    return matchAll;
  }

  /**
   * Конструктор инициализирующий почтовый ящик.
   * 
   * @param newName имя объекта
   * @deprecated необходимо использовать полный конструктор
   * @see StandartODObject(String, String, String, String)
   */
  public StandartODObject(final String name) {
    initialize(name, "An old style StandartODObject", "0.0.0", "(C) Dumb user");
  }
  
  /**
   * Конструктор инициализирующий почтовый ящик.
   * 
   * @param newName имя объекта
   * @param version версия объекта
   * @param copyright авторство
   * @param ai дополнительная информация
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
    logger = Logger.getLogger(newName);
    logger.setLevel(java.util.logging.Level.ALL);
    handlers = new HashMap();
    registerHandlers();
  }

  /**
   * Доступ к диспетчеру.
   * 
   * @return ссылка на диспетчер
   */
  protected final Dispatcher getDispatcher() {
    return dispatcher;
  }

  /**
   * Возвращает внутреннее ODISP имя объекта.
   * 
   * @return ODISP имя объекта
   */
  public final String getObjectName() {
    return name;
  }

  /**
   * Устанавливает диспетчера для текущего объекта.
   * 
   * @param newDisp диспетчер работающий с этим объектом
   */
  public final void setDispatcher(final Dispatcher newDisp) {
    this.dispatcher = newDisp;
  }

  /**
   * Установить таблицу параметров.
   * 
   * @param cfg новая таблица
   */
  public void setConfiguration(final Map cfg) {
    configuration = cfg;
  }

  /**
   * Получить значение параметра конфигурации.
   * 
   * @param name имя параметра
   */
  protected final String getParameter(final String name) {
    if (configuration != null && configuration.containsKey(name)) { return (String) configuration.get(name); }
    return null;
  }

  /**
   * Получить значение параметра конфигурации с учетом значения по-умолчанию.
   * 
   * @param name имя параметра
   * @param defValue значение по умолчанию
   */
  protected final String getParameter(final String name, final String defValue) {
    return getParameter(name) == null ? defValue : getParameter(name);
  }

  /**
   * Регистрация обработчика сообщения.
   * 
   * @param message символьное имя сообщения
   * @param handler обработчик
   */
  protected final void addHandler(final String message, final MessageHandler handler) {
    if (handlers.containsKey(message)) { return; }
    handlers.put(message, handler);
  }

  /**
   * Обработка сообщения.
   * Возможны три группы сообщений:
   * сообщения объекту,
   * сообщение посланное другому объекту или сервису в случае если стоит matchAll
   * @param msg сообщение для обработки
   */
  public final void handleMessage0(final Message msg) {
    if (blockedState && !msg.isOOB()) {
      // пропускать лишь OOB сообщения
      messages.add(msg);
      return;
    }

    if (msg.getDestination().equals(name)) {
      if (!handleMessageInternal(msg)) {
        handleMessageByObject(msg);
      }
    } else if (matchAll) {
      handleMessageByObject(msg);
    }
  }
  
  /**
   * @param msg
   */
  private void handleMessageByObject(final Message msg) {
    boolean handled = false;

    if (msg.getDestination().equals(name) && SessionManager.getSessionManager().processMessage(msg)) {
      handled = true;
    }
    if (msg.getDestination().equals(name) && handlers.containsKey(msg.getAction())) {
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
   * @param msg
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

  public void handleMessage(final Message msg) {
    logger.fine("There is no handler for message " + msg.getAction() + " in " + name);
  }

  /**
   * Наследующие классы должны перегрузить этот метод для регистрации обработчиков.
   */
  protected void registerHandlers() {
    /* DO NOTHING BY DEFAULT */
  }

  /**
   * Метод вызываемый для очистки данных класса.
   * 
   * @param type признак выхода
   * @return код возврата
   */
  public int cleanUp(final int type) {
    return 0;
  }

  /**
   * Установить состояние блокировки. В этом состоянии все сообщения, которые были получены объектом (кроме
   * сообщений с префиксом "od_"), сохраняются для последующей обработки. При смене состояния на
   * "неблокирующее" сохраненные сообщения передаются на обработку отдельному Sender-потоку.
   * 
   * @param newState новое состояние
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
  
  public Map exportState() {
    return null;
  }
  
  public void importState(final Map oldState) {
    
  }

  	/** Хранение статуса объекта. */
  protected static class ObjectStatus {
    public static final String NOERROR = "noerror";
    private final List runningTasks = new ArrayList();
    private final List completedTasks = new ArrayList();
    private final List failedTasks = new ArrayList();
    private String runningState = ObjectStatus.NOERROR;
    
    public void taskStarted(final String task) {
      runningTasks.add(task);
    }
    
    public void taskCompleted(final String task) {
      runningTasks.remove(task);
      completedTasks.add(task);
    }
    
    public void taskFailed(final String task) {
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
