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
 * Стандартный объект ODISP.
 * 
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev </a>
 * @version $Id: StandartODObject.java,v 1.20 2005/09/28 13:29:49 valeks Exp $
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
  
  private final Set providing = new HashSet();

  /**
   * Обрабатывать ли все сообщения.
   * @param newMatch true -- принимать все сообщения, false -- 
   * принимать только свои сообщения
   */
  protected final void setMatchAll(final boolean newMatch) {
    matchAll = newMatch;
  }

  /**
   * Обрабатываются ли все сообщения.
   * @return true -- принимать все сообщения, false -- 
   * принимать только свои сообщения
   */
  public final boolean getMatchAll() {
    return matchAll;
  }

  /**
   * Конструктор инициализирующий почтовый ящик.
   * 
   * @param name Имя объекта.
   * @deprecated необходимо использовать полный конструктор.
   * @see #StandartODObject(String, String, String, String)
   */
  public StandartODObject(final String name) {
    initialize(name, "An old style StandartODObject", "0.0.0", "(C) Dumb user");
  }
  
  /**
   * Конструктор инициализирующий почтовый ящик.
   * 
   * @param newName имя объекта.
   * @param fullName Полное имя объекта.
   * @param version версия объекта.
   * @param copyright авторство.
   */
  public StandartODObject(final String newName, final String fullName, final String version, final String copyright) {
    initialize(newName, fullName, version, copyright);
  }

  /**
   * Инициализация объекта.
   * 
   * @param newName Имя.
   * @param newFullName Развёрнутое описание.
   * @param newVersion Версия.
   * @param newCopyright Права.
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
   * Доступ к диспетчеру.
   * 
   * @return ссылка на диспетчер.
   */
  protected final Dispatcher getDispatcher() {
    return dispatcher;
  }

  /**
   * Возвращает внутреннее ODISP имя объекта.
   * 
   * @return ODISP имя объекта.
   */
  public final String getObjectName() {
    return name;
  }

  /**
   * Устанавливает диспетчера для текущего объекта.
   * 
   * @param newDisp диспетчер работающий с этим объектом.
   */
  public final void setDispatcher(final Dispatcher newDisp) {
    this.dispatcher = newDisp;
  }

  /**
   * Установить таблицу параметров.
   * 
   * @param cfg новая таблица.
   */
  public void setConfiguration(final Map cfg) {
    configuration = cfg;
  }

  /**
   * Получить значение параметра конфигурации.
   * 
   * @param paramName имя параметра.
   * @return значение параметра или null если он не указан
   */
  protected final String getParameter(final String paramName) {
    if (configuration != null && configuration.containsKey(paramName)) { return (String) configuration.get(paramName); }
    return null;
  }

  /**
   * Получить значение параметра конфигурации с учетом значения по-умолчанию.
   * 
   * @param paramName Имя параметра.
   * @param defValue Значение по умолчанию.
   * @return значение параметра если он установлен или значение из defValue
   */
  protected final String getParameter(final String paramName, final String defValue) {
    return getParameter(paramName) == null ? defValue : getParameter(paramName);
  }

  /**
   * Регистрация обработчика сообщения.
   * 
   * @param message Символьное имя сообщения.
   * @param handler Обработчик.
   */
  protected final void addHandler(final String message, final MessageHandler handler) {
    if (handlers.containsKey(message)) { return; }
    handlers.put(message, handler);
  }

  /**
   * Обработка сообщения.
   * Возможны три группы сообщений:
   * <ul>
   * <li>сообщения объекту;
   * <li>сообщение посланное другому объекту или сервису в случае если стоит matchAll;
   * <li>сообщения добавляются в корзину в случае;
   * </ul> 
   * 
   * @param msg сообщение для обработки.
   */
  public final void handleMessage0(final Message msg) {
    if (blockedState && !msg.isOOB()) {
      // пропускать лишь OOB сообщения
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
   * Проверка является ли сообщения нашим (место назначение - объект или
   * его сервисы).
   * 
   * @return true, если оно наше, false - в другом случае.
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
   * Обработка сообщения объектов.
   * 
   * @param msg Сообщение для обработки. 
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
   * Обработка встроенного сообщения.
   * 
   * @param msg Cообщение.
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
   * Обработчик по-умолчанию.
   * @param msg Cообщение.
   */
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
   * @param type Признак выхода.
   * @return код Возврата.
   */
  public int cleanUp(final int type) {
    return 0;
  }

  /**
   * Установить состояние блокировки. В этом состоянии все сообщения, которые были получены объектом (кроме
   * сообщений с установленным isOOB), сохраняются для последующей обработки. При смене состояния на
   * "неблокирующее" сохраненные сообщения передаются на обработку отдельному Sender-потоку.
   * 
   * @param newState Новое состояние.
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
   * Сохранение текущего состояния объекта для последующего восстановления при перезагрузке.
   * Объекту, который поддерживает перезагрузку необходимо перегрузить данных метод для
   * сохранения промежуточных данных.
   *   
   * @return Карта данных объекта. Как ключи, так данные Map <b>должны быть</b> должны реализовывать
   * интерфейс {@link java.io.Serializable}.
   */
  public Map exportState() {
    return null;
  }
  
  /**
   * Восстановление данных объекта при перезагрузке.
   * 
   * @param oldState Сохранённые данные объекта. 
   */
  public void importState(final Map oldState) {
    /* ничего не делаем по-умолчанию. */
  }

  /** Хранение статуса объекта. */
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
