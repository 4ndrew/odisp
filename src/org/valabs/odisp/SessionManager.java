package org.valabs.odisp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.doomdark.uuid.UUID;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;

/**
 * <h5>Использование ODsessionManager'а:</h5>
 * 
 * <p> Менеджер сессий хранится в памяти один раз, в рамках одного
 * пространства Odisp (для экономии использования памяти). Примерный план
 * использования ниже: 
 * 
 * <pre>
 * # После посылки сообщения: 
 * Message newMsg = dispatcher.getNewMessage(...);
 * ...
 * ODSessionManager sm = ODSessionManager.getSessionManager();
 * sm.add(newMsg.getId(), new MessageHandler() {
 *    public void messageReceived(Message msg) {
 *      System.out.println("Some reply-message received!");
 *    }
 * });
 * 
 * ...
 * 
 * handleMessage(Message msg) {
 *   ODSessionManager sm = ODSessionManager.getSessionManager();
 *   if (!sm.processMessage(msg)) {
 *     System.out.println("Message processed by SessionManager");
 *   } else if (ODObjectLoaded.equals(msg)) {
 *     ...
 *   }
 *   ...
 * }
 * </pre>
 * 
 * @author (C) 2004 <a href="dron@novel-il.ru">Андрей А. Порохин</a>
 * @version $Id: SessionManager.java,v 1.9 2004/11/05 14:11:29 valeks Exp $ 
 */
public class SessionManager {
  /** Ссылка на ODSessionManager */
  private static SessionManager sessionManager = new SessionManager();
  /** Таблица msgId <=> Обработчик (MessageHandler) */
  private Map handlers = new HashMap();
  
  /** Конструктор
   */
  protected SessionManager() {
    super();
  }
  
  /** Получить текущий менеджер
   * 
   * @return Менеджер сессий.
   */
  public static SessionManager getSessionManager() {
    return sessionManager;
  }
  
  /** Добавить обработчик в список.
   * 
   * @param messageId Идентификатор сообщения, которое было послано.
   * @see org.valabs.odisp.common.Message#getId()
   * @param messageHandler Обработчик для сообщения.
   */
  public void addMessageListener(UUID messageId,
                                 MessageHandler messageHandler) {
    handlers.put(messageId, messageHandler);
  }
  
  /** Функция обработки сообщения Odisp. рекоммендуется вызывать из
   * messageRecevied и handleMessage. 
   * @param msg Сообщение Odisp
   * @return Если в списке менеджера сессий существует обработчик для данного
   * сообщения, то возвращает true, в противном случае пришло сообщение,
   * которое не подошло ни под один ответ (необходима самостоятельная
   * обработка).
   */
  public boolean processMessage(Message msg) {
    boolean matched = false;     
    Map toPerform;
    List toRemove = new ArrayList();
    synchronized(handlers) {
      toPerform = new HashMap(handlers);
    }
    Iterator it = toPerform.keySet().iterator();
    while (it.hasNext()) {
      UUID key = (UUID) it.next();
      if (key.equals(msg.getReplyTo())) {
        ((MessageHandler) toPerform.get(key)).messageReceived(msg);
        toRemove.add(key);
      }
    } // while
    synchronized(handlers) {
      Iterator ri = toRemove.iterator();
      while (ri.hasNext()) {
        handlers.remove(ri.next());
      }
    }
    return matched;
  }

}
