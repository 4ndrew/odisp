package com.novel.odisp.common;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

/** ������� ����� ����������� �������� ���� ��� ���������
* ���������� ����������� ODISP.
* @author �������� �. ��������
* @author (�) 2003, ��� "�����-��"
* @version $Id: PollingODObject.java,v 1.16 2004/03/18 11:19:14 valeks Exp $
*/
public abstract class PollingODObject extends ODObject {
  /** ����������� �������� ODISP ��� �������.
   * @param name ��� �������
   */
  public PollingODObject(final String name) {
    super(name);
    setDaemon(true);
  }
  /** ���� ��������� ���������� ���������. */
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
	  handleMessage(m);
	}
      }
    }
  }
  /** ��������� ���������� ��������� � ����.
   * @param msg ���������
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
  /** ����� ���������� ��� ������� ������ ������.
   * @param type ������� ������
   * @return ��� ��������
   */
  public int cleanUp(final int type) {
    doExit = true;
    return 0;
  }
}
