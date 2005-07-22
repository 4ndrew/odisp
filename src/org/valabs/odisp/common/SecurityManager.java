package org.valabs.odisp.common;

import java.util.Map;

/**
 * ��������� ��������� ������������ ODISP.
 * 
 * @author (C) 2004 <a href="valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: SecurityManager.java,v 1.7 2005/07/22 13:06:56 dron Exp $
 */
public interface SecurityManager {
	/**
	 * �������� ����������� ������������ ��������� ��������.
	 * 
	 * @param userName ��� ������������.
	 * @param capability ��� ��������.
	 * @param options �������������� �����.
	 */
	boolean checkAccess(String userName, String capability, Map options);
	
  /**
   * ����������������� ������������.
   * 
   * @param name ��� ������������.
   * @param password ������ ������������.
   */
  boolean authenticateUser(final String name, final String password);
  
  /**
   * �������������� �������� ������������.
   * 
   * @param userName ��� ������������.
   * @param actionId ������������� ��������.
   * @param successStatus ��� ���������� ��������.
   * @param ai �������������� ����������.
   */
  void audit(final String userName, final String actionId, final boolean successStatus, final String ai);
}
