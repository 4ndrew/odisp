package org.valabs.odisp.common;

import java.util.Map;

/** ������� ����� ����������� �������� ���� ��� ���������
* ���������� ����������� ODISP.
* @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
* @version $Id: ODObject.java,v 1.24 2005/02/27 12:37:29 valeks Exp $
*/
public interface ODObject {
  /** ���������� ���������� ODISP ��� �������.
   * @return ODISP ��� �������
   */
  String getObjectName();
  /** ������������� ���������� ��� �������� �������.
   * @param newDisp ��������� ���������� � ���� ��������
   */
  void setDispatcher(final Dispatcher newDisp);
  /** ��������� ���������� ��������� � ����.
   * @param msg ��������� ��� ����������
   */
  void handleMessage(final Message msg);
  void handleMessage0(final Message msg);

  /** ����� ���������� ��� ������� ������ ������.
   * @param type ������� ������
   * @return ��� ��������
   */
  int cleanUp(final int type);

  /** ������ ������ ������������.
   * @return ������ ������������
   */
  String[] getDepends();
  /** ������ �������� ��������������� ��������.
   * @return ������ ��������
   */
  String[] getProviding();

  /** ���������� ������� ����������.
   * @param cfg ����� �������
   */
  void setConfiguration(final Map cfg);

  /** 
   * �������� �� ��� ��������� � �������.
   */
  boolean getMatchAll();
  
  /** �������������� ��������� ������� ��� ���������� ��� ������������.
   * @return ������, ������� ���������� ���������
   */
  Map exportState();
  
  /** ������������� ��������� ������� ����� ������������.
   * @param oldState ������ �������� ���������
   */
  void importState(Map oldState);
}
