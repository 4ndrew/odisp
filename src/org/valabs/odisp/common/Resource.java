package org.valabs.odisp.common;

import java.util.Map;

/** ��������� ���������� ������� ODISP ����.
* @author (C) 2003, <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
* @version $Id: Resource.java,v 1.6 2004/08/23 07:42:36 valeks Exp $
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
