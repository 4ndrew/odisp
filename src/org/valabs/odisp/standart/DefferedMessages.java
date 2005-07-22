package org.valabs.odisp.standart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.valabs.odisp.common.Message;
/**
 * Коллекция отложенных сообщений.
 * 
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: DefferedMessages.java,v 1.8 2005/07/22 13:06:54 dron Exp $
 */
class DefferedMessages {
  /** Карта очередей сообщений. */
  private final Map queues = new HashMap();
  
  /**
   * Добавление сообщения в определенную очередь.
   * 
   * @param objName Имя объекта (очереди).
   * @param msg Сообщение для добавления.
   */
  public final void addMessage(final String objName, final Message msg) {
    List lmessages;
    if (queues.containsKey(objName)) {
      lmessages = (List) queues.get(objName);
    } else {
      lmessages = new ArrayList();
      queues.put(objName, lmessages);
    }
    lmessages.add(msg);
  }
  
  /**
   * Возвращает список сохраненных для объекта сообщений.
   * 
   * @param objectName Имя объекта (очереди).
   * @return список Сообщений из очереди.
   */
  public final List flush(final String objectName) {
    List res;
    if (queues.containsKey(objectName)) {
      res = new ArrayList((List) queues.get(objectName));
      queues.remove(objectName);
    } else {
      res = new ArrayList();
    }
    return res;
  }
} // DefferedMessages
