package com.novel.odisp;

import com.novel.odisp.common.Message;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
/**
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: DefferedMessages.java,v 1.1 2004/02/13 00:11:29 valeks Exp $
 */
/** Коллекция отложенных сообщений. */
public class DefferedMessages {
  /** Карта очередей сообщений. */
  private Map queues = new HashMap();
  /** Добавление сообщения в определенную очередь.
   * @param objName имя объекта (очереди)
   * @param m сообщение для добавления
   */
  public final void addMessage(final String objName, final Message m) {
    if (!queues.containsKey(objName)) {
      List lmessages = new ArrayList();
      lmessages.add(m);
      queues.put(objName, lmessages);
    } else {
      ((List) queues.get(objName)).add(m);
    }
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
