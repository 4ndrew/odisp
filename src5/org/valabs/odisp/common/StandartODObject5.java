/*
 * This is a part of odisp.
 * See LICENSE for licensing details.
 */
package org.valabs.odisp.common;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.valabs.odisp.SessionManager;
import org.valabs.odisp.common.StandartODObject.ObjectStatus;
import org.valabs.stdmsg.ModuleAboutMessage;
import org.valabs.stdmsg.ModuleAboutReplyMessage;
import org.valabs.stdmsg.ModuleStatusMessage;
import org.valabs.stdmsg.ModuleStatusReplyMessage;

/** Базовый класс объектов ODISP с поддержкой Java 1.5.
 * @author (C) <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>, 2005
 * @version $Id: StandartODObject5.java,v 1.4 2006/03/29 11:39:08 valeks Exp $
 */
public abstract class StandartODObject5 implements ODObject {

  /** Журнал. */
  protected final Logger logger;

  /** Диспетчер работающий с этим объектом. */
  protected Dispatcher dispatcher;

  /** Список сообщений к обработке. */
  private final List<Message> messages = new LinkedList<Message>();

  /** Таблица конфигурационных параметров. */
  private Map configuration;

  /**
   * Есть ли необходимость обрабатывать все сообщения.
   */
  private boolean matchAll = false;

  /** Внутреннее имя объекта в ядре ODISP. */
  private final String name;
  
  private final String version;
  
  private final String copyright;
  
  protected final ObjectStatus objectStatus = new ObjectStatus();

  /** Карта обработчиков сообщений. */
  private final Map<String, Method> handlers = new HashMap<String, Method>();

  /** Признак блокировки объекта. */
  private boolean blockedState = false;

  private final String fullName;
  
  private final Set<String> providing = new HashSet<String>();
  
  private Object object;


  /**
   * @see org.valabs.odisp.common.ODObject#getObjectName()
   */
  public String getObjectName() {
    return name;
  }

  /**
   * @see org.valabs.odisp.common.ODObject#setDispatcher(org.valabs.odisp.common.Dispatcher)
   */
  public void setDispatcher(Dispatcher newDisp) {
    dispatcher = newDisp;
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
    return msg.getDestination().equals(name) || providing.contains(msg.getDestination()) || matchAll;
  }
  
  /**
   * Обработка сообщения объектов.
   * 
   * @param msg Сообщение для обработки. 
   */
  private void handleMessageByObject(final Message msg) {
    if (isOur(msg) && SessionManager.getSessionManager().processMessage(msg)) {
      return;
    }
    
    if (isOur(msg) && handlers.containsKey(msg.getAction())) {
      Method m = handlers.get(msg.getAction());
      if (m.getParameterTypes().length == 0) {
        try {
          m.invoke(object, new Object[] {});
        } catch (Exception e) {
          dispatcher.getExceptionHandler().signalException(e);
        }
        return;
      } else if (m.getParameterTypes().length == 1 && m.getParameterTypes()[0].equals(Message.class)) {
        try {
          m.invoke(object, new Object[] {
            msg,
          });
        } catch (Exception e) {
          dispatcher.getExceptionHandler().signalException(e);
        }
        return;
      } else if (m.getAnnotation(ODISPMessageHandler.class).mapping().length > 0) {
        // включено отображение полей сообщения в параметры метода
        String[] mappingNames = m.getAnnotation(ODISPMessageHandler.class).mapping();
        Object[] params = new Object[mappingNames.length + 1];
        params[0] = msg;
        for (int i = 0; i < mappingNames.length; i++) {
          params[i + 1] = msg.getField(mappingNames[i]);
        }
        try {
          m.invoke(object, params);
        } catch (Exception e) {
          dispatcher.getExceptionHandler().signalException(e);
        }
        return;
      } else {
        logger.warning("Wrong message handler found in object " + getObjectName() + " for message " + msg.getAction());
      }
    }
        
    handleMessage(msg);
  }
  
  @ODISPMessageHandler(ModuleAboutMessage.NAME)
  public void handleModuleAbout(final Message msg) {
    final Message m = dispatcher.getNewMessage();
    ModuleAboutReplyMessage.setup(m, msg.getOrigin(), name, msg.getId());
    ModuleAboutReplyMessage.setName(m, fullName);
    ModuleAboutReplyMessage.setVersion(m, version);
    ModuleAboutReplyMessage.setCopyright(m, copyright);
    dispatcher.send(m);    
  }
  
  @ODISPMessageHandler(ModuleStatusMessage.NAME)
  public void handleModuleStatus(final Message msg) {
    final Message m = dispatcher.getNewMessage();
    ModuleStatusReplyMessage.setup(m, msg.getOrigin(), name, msg.getId());
    objectStatus.setupStatusReply(m);
    dispatcher.send(m);
  }

  /**
   * Обработчик по-умолчанию.
   * @param msg Cообщение.
   */
  public void handleMessage(final Message msg) {
    logger.fine("There is no handler for message " + msg.getAction() + " in " + name);
  }

  /**
   * @see org.valabs.odisp.common.ODObject#cleanUp(int)
   */
  public int cleanUp(int type) {
    return 0;
  }

  /**
   * @see org.valabs.odisp.common.ODObject#setConfiguration(java.util.Map)
   */
  public void setConfiguration(Map cfg) {
    configuration = cfg;
  }

  /**
   * @see org.valabs.odisp.common.ODObject#getMatchAll()
   */
  public boolean getMatchAll() {
    return matchAll;
  }
  
  /** Создание списка обработчиков на основе аннотированных методов.
   * 
   * @param fromClass класс
   */
  private void registerHandlers(Class fromClass) {
    for (Method meth : fromClass.getMethods()) {
      ODISPMessageHandler omh = meth.getAnnotation(ODISPMessageHandler.class);
      if (omh != null) {
        if (!handlers.containsKey(omh.value())) {
          handlers.put(omh.value(), meth);
        } else if (meth.getDeclaringClass().equals(fromClass)){ // если это override
          handlers.put(omh.value(), meth);
        }
      }
    }
  }
  
  public StandartODObject5(final String newName, final String newFullName, final String newVersion, final String newCopyright, Class anObjectClass) {
    name = newName;
    version = newVersion;
    copyright = newCopyright;
    fullName = newFullName;
    logger = Logger.getLogger(newName);
    logger.setLevel(java.util.logging.Level.ALL);
    registerHandlers(anObjectClass);
    providing.addAll(Arrays.asList(getProviding()));
  }
  
  /**
   * Обрабатывать ли все сообщения.
   * @param newMatch true -- принимать все сообщения, false -- 
   * принимать только свои сообщения
   */
  protected final void setMatchAll(final boolean newMatch) {
    matchAll = newMatch;
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
   * Получить значение параметра конфигурации.
   * 
   * @param paramName имя параметра.
   * @return значение параметра или null если он не указан
   */
  protected final String getParameter(final String paramName) {
    if (configuration != null && configuration.containsKey(paramName)) {
      return (String) configuration.get(paramName);
    }
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

        List<Message> localMessages;

        public void run() {
          synchronized (messages) {
            localMessages = new LinkedList<Message>(messages);
            messages.clear();
          }
          
          for (Message msg: localMessages) {
            handleMessage(msg);
          }
        }
      }.start();
    }
  }
  
  /**
   * @see org.valabs.odisp.common.ODObject#exportState()
   */
  public Map exportState() {
    return null;
  }

  /**
   * @see org.valabs.odisp.common.ODObject#importState(java.util.Map)
   */
  public void importState(Map oldState) {
  }
  
  protected void setInstance(Object anObject) {
    object = anObject;
  }
}
