package org.valabs.odisp.common;

import java.util.Map;

/** ������� ����� ����������� �������� ���� ��� ���������
* ���������� ����������� ODISP.
* @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
* @version $Id: ODObject.java,v 1.23 2005/01/26 22:17:49 valeks Exp $
*/
public interface ODObject {
  /** ���������� ���������� ODISP ��� �������.
   * @return ODISP ��� �������
   */
  public String getObjectName();
  /** ������������� ���������� ��� �������� �������.
   * @param d ��������� ���������� � ���� ��������
   */
  public void setDispatcher(final Dispatcher d);
  /** ��������� ���������� ��������� � ����.
   * @param msg ��������� ��� ����������
   */
  public void handleMessage(final Message msg);
  public void handleMessage0(final Message msg);

  /** ����� ���������� ��� ������� ������ ������.
   * @param type ������� ������
   * @return ��� ��������
   */
  public int cleanUp(final int type);

  /** ������ ������ ������������.
   * @return ������ ������������
   */
  public String[] getDepends();
  /** ������ �������� ��������������� ��������.
   * @return ������ ��������
   */
  public String[] getProviding();

  /** ���������� ������� ����������.
   * @param cfg ����� �������
   */
  public void setConfiguration(final Map cfg);

  /** 
   * �������� �� ��� ��������� � �������.
   */
  public boolean getMatchAll();
  
  /** �������������� ��������� ������� ��� ���������� ��� ������������.
   * @return ������, ������� ���������� ���������
   */
  public Map exportState();
  
  /** ������������� ��������� ������� ����� ������������.
   * @param oldState ������ �������� ���������
   */
  public void importState(Map oldState);
}
