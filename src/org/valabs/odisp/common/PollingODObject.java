package com.novel.odisp.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/** Базовый класс реализующий почтовый ящик для сообщений
* посылаемых диспетчером ODISP.
* @author Валентин А. Алексеев
* @author (С) 2003, НПП "Новел-ИЛ"
* @version $Id: PollingODObject.java,v 1.20 2004/04/02 11:37:38 valeks Exp $
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
	    // небольшой хак для того, что бы получить список вызовов методов
	    StackTraceElement[] stea = {};
	    try {
	      throw new Exception();
	    } catch (Exception e) {
	      stea = e.getStackTrace();
	    }
	    String stackTraceMessage = "\nStack trace:";
	    for(int i = 1; i < 15 && i < stea.length; i++) {
	      stackTraceMessage += stea[i] + "\n";
	    }
            logger.severe("BOOO!!!! PollingODObject::run() m == null. It is impossible.\n" + stackTraceMessage);
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
      // небольшой хак для того, что бы получить список вызовов методов
      StackTraceElement[] stea = {};
      try {
	throw new Exception();
      } catch (Exception e) {
	stea = e.getStackTrace();
      }
      String stackTraceMessage = "\nStack trace:";
      for(int i = 1; i < 15 && i < stea.length; i++) {
	stackTraceMessage += stea[i] + "\n";
      }
      System.err.println("BOOO!!!!!\n" + stackTraceMessage);
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
