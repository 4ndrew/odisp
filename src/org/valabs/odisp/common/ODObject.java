package com.novel.odisp.common;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

/** ������� ����� ����������� �������� ���� ��� ���������
* ���������� ����������� ODISP.
* @author �������� �. ��������
* @author (�) 2003, ��� "�����-��"
* @version $Id: ODObject.java,v 1.11 2003/11/15 19:14:45 valeks Exp $
*/
public abstract class ODObject extends Thread {
  /** ������ */
  protected Logger logger;
  /** ��������� ���������� � ���� �������� */
  protected Dispatcher dispatcher;
  /** ������ ��������� � ��������� */
  protected List messages;
  /** ������� ��������� ������ ��������� ����� ��������� ��������� */
  protected boolean doExit;
  /** Regex ����� ����������� ���������. �� ��������� ���������������� ������ �������. */
  protected String match;
  /** ���������� ��� ������� � ���� ODISP */
  public String name;
  /** �������� ����� ����������� ���������
   * @param match ����� �����
   */
  protected void setMatch(String match) {
    this.match = match;
  }
  /** ����������� ���������������� �������� ����
   * @param name ��� �������
   */
  public ODObject(String name) {
    super(name);
    messages = new ArrayList();
    this.name = name;
    this.match = name;
    logger = Logger.getLogger(name);
  }
  /** ������ � ���������� 
   * @return ������ �� ���������
   */
  protected Dispatcher getDispatcher() {
    return dispatcher;
  }
  /** ���������� ���������� ODISP ��� ������� 
   * @return ODISP ��� �������
   */
  public String getObjectName() {
    return name;
  }
  /** ������������� ���������� ��� �������� ������� 
   * @param d ��������� ���������� � ���� ��������
   */
  public void setDispatcher(Dispatcher d) {
    this.dispatcher = d;
  }
  /** ��������� ���������� ��������� � ���� 
   * @param msg ��������� ��� ����������
   */
  public abstract void addMessage(Message msg);
  /** ���������� ������ ��������� � ���� 
   * @param messages ������ ��������� ��� ����������
   */
  public void addMessages(List messages) {
    Iterator it = messages.iterator();
    while (it.hasNext()) {
		addMessage((Message) it.next());
    }
  }	
  /** ����� ������� �������� ��� ��������� ���������� ���������.
   * ��������� ������� ����������� ���� ����� ��� ���������� ������.
   * @param msg ��������� ��� ���������
   */
  protected abstract void handleMessage(Message msg);
  /** ����� ���������� ��� ������� ������ ������ 
   * @param type ������� ������
   * @return ��� ��������
   */
  public abstract int cleanUp(int type);
  /** ����� ���������� ��������� �� �����
   * @param place ������� ���� � �������� ��������� ���������
   * @param msg ���������
   * @deprecated ���������� ������������ logger
   */
  protected void log(String place, String msg) {
    logger.fine(getObjectName() + "." + place + ": " + msg);
  }
  /** ������ ������ ������������
   * @return ������ ������������
   */
  public String[] getDepends() {
    String res[] = {};
    return res;
  }
  /** ������ �������� ��������������� ��������
   * @return ������ ��������
   */
  public abstract String[] getProviding();
}
