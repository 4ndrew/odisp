package org.valabs.odisp.common;

import java.util.Map;

/** ������� ����� ����������� �������� ���� ��� ���������
* ���������� ����������� ODISP.
* @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
* @version $Id: ODObject.java,v 1.21 2004/08/23 07:42:37 valeks Exp $
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

  /** ������� RegEx ��������� ��� �������� ������������ ����������.
   * @return ������ � regex ����������
   */
  public String getMatch();
}
