package com.novel.odisp.common;

/** ��������� ���������� ������� ODISP ����.
* @author �������� �. ��������
* @author (C) 2003, ��� "�����-��"
* @version $Id: Resource.java,v 1.2 2004/01/16 10:29:15 valeks Exp $
*/
public interface Resource {
  /** ������������ ���������� ������ ������������ �������������� ��������.
   * @return ������������ ���-�� ������
   */
  int getMaxReferenceCount();

  /** ���������� ��� ������������� �������� �������.
   * @param type ������� ������
   * @return ��� ��������
   */
  int cleanUp(int type);
}
