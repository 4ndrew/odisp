package com.novel.odisp;

import com.novel.odisp.common.Message;
import com.novel.odisp.common.ODObject;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/** Реализация единого потока рассылки и обработки сообщений.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: Sender.java,v 1.1 2004/05/12 07:44:06 valeks Exp $
 */
public class Sender extends Thread {
  /** Список сообщений для отсылки. */
  private List messages = new ArrayList();
  /** Количество сообщений в очереди (что бы лишний раз не блокировать messages).*/
  private int messageCounter = 0;
  /** Счетчик потоков. */
  private static int counter = 0;
  /** Условие окончания обработки. */
  private boolean doExit = false;
  /** Стандартный конструктор. */
  public Sender() {
    super("Sender thread " + (counter++));
    setDaemon(true);
    start();
  }

  /** Добавление сообщения в очередь обработки.
   * @param msg сообщение
   * @param obj объект
   */
  public final synchronized void send(final Message msg, final ODObject obj) {
    messageCounter++;
    messages.add(new SendRecord(msg, obj));
    notify();
  }

  /** Принудительное завершение обработки сообщений. */
  public final synchronized void quit() {
    doExit = true;
    notify();
  }

  /** Основной метод нитки, который отсылает сообщений. */
  public void run() {
    while (!doExit) {
      try {
	synchronized(this) {
	  wait(1000);
	}
      } catch (InterruptedException e) {
	doExit = true;
      }
      List localMessages = null;
      synchronized(messages) {
	localMessages = new ArrayList(messages);
	messages.clear();
      }
      if (localMessages != null && localMessages.size() > 0) {
	Iterator it = localMessages.iterator();
	while(it.hasNext()) {
	  SendRecord sr = (SendRecord) it.next();
	  sr.getObject().addMessage(sr.getMessage());
	  messageCounter--;
	}
      }
    }
  }

  /** Доступ к счетчику сообщений в очереди. */
  public int getCounter() {
    return messageCounter;
  }

  /** Хранит информацию о каждом передаваемом сообщении. */
  private class SendRecord {
    /** Сообщение. */
    private Message msg;
    /** Доступ к сообщению. */
    public Message getMessage() {
      return msg;
    }
    /** Объект. */
    private ODObject obj;
    /** Доступ к ссылке на объект. */
    public ODObject getObject() {
      return obj;
    }
    /** Создание записи для заданных полей.
     * @param nmsg сообщение
     * @param nobj объект
     */
    public SendRecord(final Message nmsg, final ODObject nobj) {
      msg = nmsg;
      obj = nobj;
    }
  } // SendRecord
} // Sender
