package com.novel.odisp.common;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

/** ������� ����� ����������� �������� ���� ��� ���������
* ���������� ����������� ODISP.
* @author �������� �. ��������
* @author (�) 2003, ��� "�����-��"
* @version $Id: PollingODObject.java,v 1.6 2003/11/15 19:21:41 valeks Exp $
*/
public abstract class PollingODObject extends ODObject {
  /** ����������� �������� ODISP ��� ������� 
   * @param name ��� �������
   */
  public PollingODObject(String name) {
    super(name);
  }
  /** ���� ��������� ���������� ��������� */
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
	  logger.finest("processing " + m);
	  handleMessage(m);
	}
      }
    }
  }
  /** ��������� ���������� ��������� � ���� 
   * @param msg ���������
   */
  public final void addMessage(Message msg) {
    if (!Pattern.matches(match, msg.getDestination())) {
		return;
    }
    synchronized (this) {
      messages.add(msg);
    }
  }
  /** ����� ���������� ��� ������� ������ ������ 
   * @param type ������� ������
   * @return ��� ��������
   */
  public int cleanUp(int type) {
    doExit = true;
    return 0;
  }
}
