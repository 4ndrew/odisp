package org.valabs.odisp.common;

import java.util.Map;

/** Интерфейс менеджера безопасности ODISP.
 * @author (C) 2004 <a href="valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: SecurityManager.java,v 1.4 2004/11/15 11:54:53 valeks Exp $
 */
public interface SecurityManager {
	/** Проверка доступности пользователю заданного свойства.
	 * @param userName имя пользователя
	 * @param capabilityName имя свойства
	 * @param options Дополнительные опции
	 */
	boolean checkAccess(String userName, String capabilityName, Map options);	
  /** Аутентифицировать пользователя.
   * @param name имя пользователя
   * @param password пароль пользователя
   */
  boolean authenticateUser(final String name, final String password);
}
