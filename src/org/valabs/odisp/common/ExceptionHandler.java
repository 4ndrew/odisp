package org.valabs.odisp.common;

/** Интерфейс обработчиков ошибок.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ExceptionHandler.java,v 1.3 2004/08/23 07:42:37 valeks Exp $
 */
public interface ExceptionHandler {
	void signalException(Exception e);
}
