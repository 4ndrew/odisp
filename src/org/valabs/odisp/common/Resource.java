package com.novel.odisp.common;

import java.util.Map;

/** ��������� ���������� ������� ODISP ����.
* @author (C) 2003, <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
* @version $Id: Resource.java,v 1.5 2004/08/18 12:48:39 valeks Exp $
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
  /** ���������� ������������.
   * @param configuration ������������
   */
  void setConfiguration(Map configuration);
}
