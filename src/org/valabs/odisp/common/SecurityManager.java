package com.novel.odisp.common;

import java.util.Map;

/** Интерфейс менеджера безопасности ODISP.
 * @author <a href="valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: SecurityManager.java,v 1.1 2004/07/01 13:45:39 valeks Exp $
 */
public interface SecurityManager {
	/** Проверка доступности пользователю заданного свойства.
	 * @param userName имя пользователя
	 * @param capabilityName имя свойства
	 * @param options Дополнительные опции
	 */
	boolean checkAccess(String userName, String capabilityName, Map options);	
}