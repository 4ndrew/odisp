/* ODISP -- Message Oriented Middleware
 * Copyright (C) 2003-2005 Valentin A. Alekseev
 * Copyright (C) 2003-2005 Andrew A. Porohin 
 * 
 * ODISP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.1 of the License.
 * 
 * ODISP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ODISP.  If not, see <http://www.gnu.org/licenses/>.
 */
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
