package org.valabs.odisp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.doomdark.uuid.UUID;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;

/**
 * <h5>Использование ODsessionManager'а:</h5>
 * <p>
 * Менеджер сессий хранится в памяти один раз, в рамках одного пространства Odisp (для экономии использования
 * памяти). Примерный план использования ниже:
 * 
 * <pre>
 * 
 *  
 *   
 *    
 *     
 *      
 *       # После посылки сообщения: 
 *       Message newMsg = dispatcher.getNewMessage(...);
 *       ...
 *       ODSessionManager sm = ODSessionManager.getSessionManager();
 *       sm.add(newMsg.getId(), new MessageHandler() {
 *          public void messageReceived(Message msg) {
 *            System.out.println(&quot;Some reply-message received!&quot;);
 *          }
 *       });
 *       
 *       ...
 *       
 *       handleMessage(Message msg) {
 *         ODSessionManager sm = ODSessionManager.getSessionManager();
 *         if (!sm.processMessage(msg)) {
 *           System.out.println(&quot;Message processed by SessionManager&quot;);
 *         } else if (ODObjectLoaded.equals(msg)) {
 *           ...
 *         }
 *         ...
 *       }
 *       
 *      
 *     
 *    
 *   
 *  
 * </pre>
 * 
 * @author (C) 2004 <a href="dron@novel-il.ru">Андрей А. Порохин </a>
 * @version $Id: SessionManager.java,v 1.13 2005/02/17 12:31:44 valeks Exp $
 */
public class SessionManager {

  /** Ссылка на ODSessionManager */
  private static SessionManager sessionManager = new SessionManager();

  /** Список обработчиков. */
  private List handlers = Collections.synchronizedList(new ArrayList());

  /**
   * Конструктор
   */
  protected SessionManager() {
    super();
  }

  /**
   * Получить текущий менеджер
   * 
   * @return Менеджер сессий.
   */
  public static SessionManager getSessionManager() {
    return sessionManager;
  }

  /**
   * Добавить обработчик в список. По-умолчанию -- добавление с автоматическим удаление после обработки.
   * 
   * @param messageId Идентификатор сообщения, которое было послано.
   * @see org.valabs.odisp.common.Message#getId()
   * @param messageHandler Обработчик для сообщения.
   */
  public void addMessageListener(UUID messageId, MessageHandler messageHandler) {
    addMessageListener(messageId, messageHandler, false);
  }

  /**
   * Добавление обработчика для указанного сообщения. В отличие от предыдущего варианта если параметр
   * (multiply установлен в true) после обработки сообщения обработчиком запись о сессии из SessionManager не
   * удаляется. Для того, что бы удалить её вручную необходимо воспользоваться методом removeMessageListener.
   * 
   * @param messageId идентификатор ответа сообщения
   * @param messageHandler обработчик сообщения
   * @param multiply признак множественности
   * @see SessionManager#removeMessageListener(UUID, MessageHandler)
   */
  public void addMessageListener(UUID messageId, MessageHandler messageHandler, boolean multiply) {
    handlers.add(new SessionRecord(messageId, messageHandler, multiply));
  }

  /**
   * Удаление обработчика сообщения сессии.
   * 
   * @param messageId идентификатор ответа сообщения
   * @param messageHandler обработчик сообщения
   * @see SessionManager#addMessageListener(UUID, MessageHandler, boolean)
   */
  public void removeMessageListener(UUID messageId, MessageHandler messageHandler) {
    Iterator it = handlers.iterator();
    while (it.hasNext()) {
      SessionRecord element = (SessionRecord) it.next();
      if (element.getMsgId().equals(messageId) && element.getMessageHandler().equals(messageHandler)) {
        it.remove();
      }
    }
  }

  /**
   * Функция обработки сообщения Odisp. рекоммендуется вызывать из messageRecevied и handleMessage.
   * 
   * @param msg Сообщение Odisp
   * @return Если в списке менеджера сессий существует обработчик для данного сообщения, то возвращает true, в
   *         противном случае пришло сообщение, которое не подошло ни под один ответ (необходима
   *         самостоятельная обработка).
   */
  public boolean processMessage(Message msg) {
    boolean matched = false;
    Iterator it = handlers.iterator();
    List toPerform = new ArrayList();
    List toRemove = new ArrayList();
    while (it.hasNext()) {
      SessionRecord element = (SessionRecord) it.next();
      if (element.getMsgId().equals(msg.getReplyTo())) {
        toPerform.add(element);
        if (!element.isMultiply()) {
          toRemove.add(element);
        }
        matched = true;
      }
    }
    it = toPerform.iterator();
    while (it.hasNext()) {
      SessionRecord element = (SessionRecord) it.next();
      element.getMessageHandler().messageReceived(msg);
    }
    it = toRemove.iterator();
    while (it.hasNext()) {
      Object element = it.next();
      handlers.remove(element);
    }
    return matched;
  }

  class SessionRecord {

    private UUID msgId;

    private MessageHandler messageHandler;

    private boolean multiply;

    SessionRecord(UUID _msgId, MessageHandler _messageHandler, boolean _multiply) {
      msgId = _msgId;
      messageHandler = _messageHandler;
      multiply = _multiply;
    }

    MessageHandler getMessageHandler() {
      return messageHandler;
    }

    UUID getMsgId() {
      return msgId;
    }

    boolean isMultiply() {
      return multiply;
    }
  }
}