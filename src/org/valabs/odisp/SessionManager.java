package com.novel.odisp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.novel.odisp.common.Message;
import com.novel.odisp.common.MessageHandler;

/** Класс для работы с Odisp сессиями ("запрос-ответ"). Временно помещён в
 * клиентскую часть проекта, в будующем (просле согласования) будет перенесён
 * в Odisp респозиторий. 
 * 
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
 *   } else if (msg instanceof ODObjectLoaded) {
 *     ...
 *   }
 *   ...
 * }
 * </pre>
 * 
 * @author <a href="dron@novel-il.ru">Андрей А. Порохин</a>
 * @author (C) 2004 НПП "Новел-ИЛ"
 * @version $Id: SessionManager.java,v 1.1 2004/04/14 13:10:03 dron Exp $ 
 */
public class SessionManager {
  /** Ссылка на ODSessionManager */
  private static SessionManager sessionManager = new SessionManager();
  /** Таблица msgId <=> Обработчик (MessageHandler) */
  private Map handlers = new HashMap();
  
  /** Конструктор
   */
  public SessionManager() {
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
   * @see com.novel.odisp.common.Message#getId()
   * @param messageListener Обработчик для сообщения.
   */
  public void addMessageListener(int messageId,
                                 MessageHandler messageHandler) {
    handlers.put(new Integer(messageId), messageHandler);
  }
  
  /** Функция обработки сообщения Odisp. рекоммендуется вызывать из
   * messageRecevied и handleMessage. 
   * 
   * @see com.novel.odisp.common.PollingODObject#messageReceived(Message)
   * @see com.novel.odisp.common.CallbackODObject#handleMessage(Message) 
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
      Integer key = (Integer) it.next();
      if (key.equals(new Integer(msg.getReplyTo()))) {
        ((MessageHandler) toPerform.get(key)).messageReceived(msg);
        toRemove.add(key);
      }
    } // while
    synchronized(handlers) {
      Iterator ri = toRemove.iterator();
      while (it.hasNext()) {
        handlers.remove(it.next());
      }
    }
    return matched;
  }

}