package com.novel.odisp.common;

import java.util.Map;

/** ��������� ��������� ������������ ODISP.
 * @author (C) 2004 <a href="valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: SecurityManager.java,v 1.2 2004/08/18 12:48:40 valeks Exp $
 */
public interface SecurityManager {
	/** �������� ����������� ������������ ��������� ��������.
	 * @param userName ��� ������������
	 * @param capabilityName ��� ��������
	 * @param options �������������� �����
	 */
	boolean checkAccess(String userName, String capabilityName, Map options);	
}