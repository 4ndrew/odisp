package org.valabs.odisp.standart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.valabs.odisp.common.Message;
/**
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: DefferedMessages.java,v 1.6 2004/08/23 07:42:37 valeks Exp $
 */
/** Коллекция отложенных сообщений. */
class DefferedMessages {
  /** Карта очередей сообщений. */
  private Map queues = new HashMap();
  /** Добавление сообщения в определенную очередь.
   * @param objName имя объекта (очереди)
   * @param m сообщение для добавления
   */
  public final void addMessage(final String objName, final Message m) {
    List lmessages;
    if (!queues.containsKey(objName)) {
      lmessages = new ArrayList();
      queues.put(objName, lmessages);
    } else {
      lmessages = (List) queues.get(objName);
    }
    lmessages.add(m);
  }
  /** Возвращает список сохраненных для объекта сообщений.
   * @param objectName имя объекта (очереди)
   * @return список сообщений из очереди
   */
  public final List flush(final String objectName) {
    if (queues.containsKey(objectName)) {
      List res = new ArrayList((List) queues.get(objectName));
      queues.remove(objectName);
      return res;
    } else {
      return new ArrayList();
    }
  }
} // DefferedMessages
