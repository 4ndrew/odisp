package org.valabs.odisp.common;

import java.util.Map;

/** ��������� ��������� ������������ ODISP.
 * @author (C) 2004 <a href="valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: SecurityManager.java,v 1.4 2004/11/15 11:54:53 valeks Exp $
 */
public interface SecurityManager {
	/** �������� ����������� ������������ ��������� ��������.
	 * @param userName ��� ������������
	 * @param capabilityName ��� ��������
	 * @param options �������������� �����
	 */
	boolean checkAccess(String userName, String capabilityName, Map options);	
  /** ����������������� ������������.
   * @param name ��� ������������
   * @param password ������ ������������
   */
  boolean authenticateUser(final String name, final String password);
}
