package com.novel.odisp.common;

/** ��������� ���������� ������� ODISP ����
* @author �������� �. ��������
* @author (C) 2003, ��� "�����-��"
* @version $Id: Resource.java,v 1.1 2003/12/03 19:16:17 valeks Exp $
*/
public interface Resource {
  /** ������������ ���������� ������ ������������ �������������� �������� 
   * @return ������������ ���-�� ������
   */
  int getMaxReferenceCount();
  /** ���������� ��� ������������� �������� ������� 
   * @param type ������� ������
   * @return ��� ��������
   */
  int cleanUp(int type);
}
