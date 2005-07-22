package org.valabs.odisp.common;

import java.util.Map;

/**
 * ��������� ���������� ������� ODISP ����.
 * 
 * @author (C) 2003, <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
 * @version $Id: Resource.java,v 1.8 2005/07/22 13:06:56 dron Exp $
 */
public interface Resource {
  /**
   * ������������ ���������� ������ ������������ �������������� ��������.
   * 
   * @return ������������ ���-�� ������.
   */
  int getMaxReferenceCount();

  /**
   * ���������� ��� ������������� �������� �������.
   * 
   * @param type ������� ������.
   * @return ��� ��������.
   */
  int cleanUp(int type);
  
  /**
   * ���������� ������������.
   * 
   * @param config ������������.
   */
  void setConfiguration(Map config);
}
