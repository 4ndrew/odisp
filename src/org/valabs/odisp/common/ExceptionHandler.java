package org.valabs.odisp.common;

/** Интерфейс обработчиков ошибок.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ExceptionHandler.java,v 1.4 2005/02/27 12:37:29 valeks Exp $
 */
public interface ExceptionHandler {
	void signalException(Exception exception);
}
