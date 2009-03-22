package org.valabs.odisp.common;

import java.util.Map;

/**
 * Интерфейс менеджера безопасности ODISP.
 * 
 * @author (C) 2004 <a href="valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: SecurityManager.java,v 1.8 2005/07/22 15:32:02 valeks Exp $
 */
public interface SecurityManager {
	/**
	 * Проверка доступности пользователю заданного свойства.
	 * 
	 * @param userName имя пользователя.
	 * @param capability имя свойства.
	 * @param options Дополнительные опции.
	 * @return флаг успеха или отказа проверки
	 */
	boolean checkAccess(String userName, String capability, Map options);
	
  /**
   * Аутентифицировать пользователя.
   * 
   * @param name имя пользователя.
   * @param password пароль пользователя.
   * @return флаг успеха или отказа проверки
   */
  boolean authenticateUser(final String name, final String password);
  
  /**
   * Журналирование операции пользователя.
   * 
   * @param userName Имя пользователя.
   * @param actionId Идентификатор операции.
   * @param successStatus Код результата операции.
   * @param ai Дополнительная информация.
   */
  void audit(final String userName, final String actionId, final boolean successStatus, final String ai);
}
