package org.valabs.odisp;

import java.util.ArrayList;
import java.util.Collections;
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
 * @version $Id: SessionManager.java,v 1.11 2004/12/01 10:32:59 boris Exp $
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
    List toDelete = new ArrayList();

    for (int i = 0; i < handlers.size(); i++) {
      SessionRecord arecord = (SessionRecord) handlers.get(i);
      if (arecord.getMsgId().equals(messageId) && arecord.getMessageHandler().equals(messageHandler)) {
        toDelete.add(new Integer(i));
      }
    }
    if (toDelete.size() > 0) {
      for (int i = 0; i < toDelete.size(); i++) {
        handlers.remove(((Integer) toDelete.get(i)).intValue());
      }
    }

    //    List tmp;
    //    synchronized (handlers) {
    //      tmp = new ArrayList(handlers);
    //    }
    //    Iterator it = tmp.iterator();
    //    while (it.hasNext()) {
    //      SessionRecord rec = (SessionRecord) it.next();
    //      if (rec.getMsgId().equals(messageId) && rec.getMessageHandler().equals(messageHandler)) {
    //        it.remove();
    //      }
    //    }
    //    synchronized (handlers) {
    //      handlers = tmp;
    //    }
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

    List toDelete = new ArrayList();
    List toPerform = new ArrayList();
    for (int i = 0; i < handlers.size(); i++) {
      SessionRecord arecord = (SessionRecord) handlers.get(i);
      if (arecord.getMsgId().equals(msg.getReplyTo())) {
        if (!arecord.isMultiply()) {
          toDelete.add(new Integer(i));
        }
        toPerform.add(((SessionRecord) handlers.get(i)).getMessageHandler());
        matched = true;
      }
    }
    if (matched) {
      for (int i = 0; i < toDelete.size(); i++) {
        handlers.remove(((Integer) toDelete.get(i)).intValue());
      }
      for (int i = 0; i < toPerform.size(); i++) {
        ((MessageHandler) toPerform.get(i)).messageReceived(msg);
      }
    }

    //    List toPerform;
    //    synchronized (handlers) {
    //      toPerform = new ArrayList(handlers);
    //    }
    //    Iterator it = toPerform.iterator();
    //    while (it.hasNext()) {
    //      SessionRecord arecord = (SessionRecord) it.next();
    //      if (arecord.getMsgId().equals(msg.getReplyTo())) {
    //        arecord.getMessageHandler().messageReceived(msg);
    //        if (!arecord.isMultiply()) {
    //          it.remove();
    //        }
    //        matched = true;
    //      }
    //    } // while
    //    synchronized (handlers) {
    //      handlers = toPerform;
    //    }

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