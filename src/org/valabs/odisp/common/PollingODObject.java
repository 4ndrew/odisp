package com.novel.odisp.common;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

/** Базовый класс реализующий почтовый ящик для сообщений
* посылаемых диспетчером ODISP.
* @author Валентин А. Алексеев
* @author (С) 2003, НПП "Новел-ИЛ"
* @version $Id: PollingODObject.java,v 1.18 2004/03/26 21:53:38 valeks Exp $
*/
public abstract class PollingODObject extends ODObject {
  /** Конструктор задающий ODISP имя объекта.
   * @param name имя объекта
   */
  public PollingODObject(final String name) {
    super(name);
    setDaemon(true);
  }
  /** Цикл обработки приходящих сообщений. */
  public final void run() {
    logger.finest("message processing loop started");
    while (!doExit) {
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
          if (m == null) {
            logger.severe("BOOO!!!! PollingODObject::run() m == null. It is impossible");
          } else {
	        handleMessage(m);
          }
	    }
      }
    }
  }
  /** Интерфейс добавления сообщения в ящик.
   * @param msg сообщение
   */
  public final void addMessage(final Message msg) {
    if (msg == null) {
      System.err.println("BOOO!!!!!");
      return;
    }
    if (!Pattern.matches(match, msg.getDestination())
	&& !Pattern.matches(msg.getDestination(), getObjectName())) {
		return;
    }
    synchronized (this) {
      messages.add(msg);
      notify();
    }
  }
  /** Метод вызываемый для очистки данных класса.
   * @param type признак выхода
   * @return код возврата
   */
  public int cleanUp(final int type) {
    doExit = true;
    return 0;
  }
}
