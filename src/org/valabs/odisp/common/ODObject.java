package com.novel.odisp.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/** ������� ����� ����������� �������� ���� ��� ���������
* ���������� ����������� ODISP.
* @author �������� �. ��������
* @author (�) 2003, ��� "�����-��"
* @version $Id: ODObject.java,v 1.16 2004/05/11 09:55:39 valeks Exp $
*/
public abstract class ODObject /* extends Thread */ {
  /** ������. */
  protected Logger logger;
  /** ��������� ���������� � ���� ��������. */
  protected Dispatcher dispatcher;
  /** ������ ��������� � ���������. */
  protected List messages;
  /** ������� ���������������� ����������. */
  private Map configuration;
  /** Regex ����� ����������� ���������.
   * �� ��������� ���������������� ������ �������. */
  protected String match;
  /** ���������� ��� ������� � ���� ODISP. */
  public String name;
  /** �������� ����� ����������� ���������.
   * @param newMatch ����� �����
   */
  protected final void setMatch(final String newMatch) {
    match = newMatch;
  }
  /** ����������� ���������������� �������� ����.
   * @param newName ��� �������
   */
  public ODObject(final String newName) {
    messages = new ArrayList();
    name = newName;
    match = newName;
    logger = Logger.getLogger(newName);
  }
  /** ������ � ����������.
   * @return ������ �� ���������
   */
  protected final Dispatcher getDispatcher() {
    return dispatcher;
  }
  /** ���������� ���������� ODISP ��� �������.
   * @return ODISP ��� �������
   */
  public final String getObjectName() {
    return name;
  }
  /** ������������� ���������� ��� �������� �������.
   * @param d ��������� ���������� � ���� ��������
   */
  public final void setDispatcher(final Dispatcher d) {
    this.dispatcher = d;
  }
  /** ��������� ���������� ��������� � ����.
   * @param msg ��������� ��� ����������
   */
  public abstract void addMessage(final Message msg);

  /** ���������� ������ ��������� � ����.
   * @param newMessages ������ ��������� ��� ����������
   */
  public final void addMessages(final List newMessages) {
    Iterator it = newMessages.iterator();
    while (it.hasNext()) {
		addMessage((Message) it.next());
    }
  }
  /** ����� ������� �������� ��� ��������� ���������� ���������.
   * ��������� ������� ����������� ���� ����� ��� ���������� ������.
   * @param msg ��������� ��� ���������
   */
  protected abstract void handleMessage(final Message msg);

  /** ����� ���������� ��� ������� ������ ������.
   * @param type ������� ������
   * @return ��� ��������
   */
  public abstract int cleanUp(final int type);
  /** ����� ���������� ��������� �� �����.
   * @param place ������� ���� � �������� ��������� ���������
   * @param msg ���������
   * @deprecated ���������� ������������ logger
   */
  protected final void log(final String place, final String msg) {
    logger.fine(getObjectName() + "." + place + ": " + msg);
  }
  /** ������ ������ ������������.
   * @return ������ ������������
   */
  public abstract String[] getDepends();
  /** ������ �������� ��������������� ��������.
   * @return ������ ��������
   */
  public abstract String[] getProviding();
  /** ���������� ������� ����������.
   * @param cfg ����� �������
   */
  public final void setConfiguration(final Map cfg) {
    configuration = cfg;
  }
  /** �������� �������� ��������� ������������.
   * @param name ��� ���������
   */
  protected final String getParameter(final String name) {
    if (configuration != null && configuration.containsKey(name)) {
      return (String) configuration.get(name);
    }
    return null;
  }
  /** �������� �������� ��������� ������������ � ������ �������� ��-���������.
   * @param name ��� ���������
   * @param defValue �������� �� ���������
   */
  protected final String getParameter(final String name, final String defValue) {
    return getParameter(name) == null ? defValue : getParameter(name);
  }
}
