package com.novel.odisp;

import com.novel.odisp.common.Message;
import com.novel.odisp.common.ODObject;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/** ���������� ������� ������ �������� � ��������� ���������.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: Sender.java,v 1.1 2004/05/12 07:44:06 valeks Exp $
 */
public class Sender extends Thread {
  /** ������ ��������� ��� �������. */
  private List messages = new ArrayList();
  /** ���������� ��������� � ������� (��� �� ������ ��� �� ����������� messages).*/
  private int messageCounter = 0;
  /** ������� �������. */
  private static int counter = 0;
  /** ������� ��������� ���������. */
  private boolean doExit = false;
  /** ����������� �����������. */
  public Sender() {
    super("Sender thread " + (counter++));
    setDaemon(true);
    start();
  }

  /** ���������� ��������� � ������� ���������.
   * @param msg ���������
   * @param obj ������
   */
  public final synchronized void send(final Message msg, final ODObject obj) {
    messageCounter++;
    messages.add(new SendRecord(msg, obj));
    notify();
  }

  /** �������������� ���������� ��������� ���������. */
  public final synchronized void quit() {
    doExit = true;
    notify();
  }

  /** �������� ����� �����, ������� �������� ���������. */
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

  /** ������ � �������� ��������� � �������. */
  public int getCounter() {
    return messageCounter;
  }

  /** ������ ���������� � ������ ������������ ���������. */
  private class SendRecord {
    /** ���������. */
    private Message msg;
    /** ������ � ���������. */
    public Message getMessage() {
      return msg;
    }
    /** ������. */
    private ODObject obj;
    /** ������ � ������ �� ������. */
    public ODObject getObject() {
      return obj;
    }
    /** �������� ������ ��� �������� �����.
     * @param nmsg ���������
     * @param nobj ������
     */
    public SendRecord(final Message nmsg, final ODObject nobj) {
      msg = nmsg;
      obj = nobj;
    }
  } // SendRecord
} // Sender
