package com.novel.odisp.common;

import java.util.Map;

/** ������� ����� ����������� �������� ���� ��� ���������
* ���������� ����������� ODISP.
* @author �������� �. ��������
* @author (�) 2003, ��� "�����-��"
* @version $Id: ODObject.java,v 1.18 2004/05/13 11:42:20 valeks Exp $
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
  public void addMessage(final Message msg);

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
}
