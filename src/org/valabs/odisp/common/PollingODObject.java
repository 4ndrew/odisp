package com.novel.odisp.common;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

/** Базовый класс реализующий почтовый ящик для сообщений
* посылаемых диспетчером ODISP.
* @author Валентин А. Алексеев
* @author (С) 2003, НПП "Новел-ИЛ"
* @version $Id: PollingODObject.java,v 1.7 2003/11/22 10:21:19 valeks Exp $
*/
public abstract class PollingODObject extends ODObject {
  /** Конструктор задающий ODISP имя объекта 
   * @param name имя объекта
   */
  public PollingODObject(String name) {
    super(name);
  }
  /** Цикл обработки приходящих сообщений */
  public final void run() {
    logger.finest("message processing loop started");
    while (doExit != true) {
      List localMessages;
      synchronized (this) {
	try {
	  wait(1000);
	} catch (InterruptedException e) { /*NOP*/ }
      }
      synchronized (messages) {
	localMessages = new ArrayList(messages);
	messages.clear();
      }
      if (localMessages != null && localMessages.size() > 0) {
	Iterator mIter = localMessages.iterator();
	while (mIter.hasNext()) {
	  Message m = (Message) mIter.next();
	  handleMessage(m);
	}
      }
    }
  }
  /** Интерфейс добавления сообщения в ящик 
   * @param msg сообщение
   */
  public final void addMessage(Message msg) {
    if (!Pattern.matches(match, msg.getDestination()) && !Pattern.matches(msg.getDestination(), match)) {
		return;
    }
    synchronized (this) {
      messages.add(msg);
    }
  }
  /** Метод вызываемый для очистки данных класса 
   * @param type признак выхода
   * @return код возврата
   */
  public int cleanUp(int type) {
    doExit = true;
    return 0;
  }
}
