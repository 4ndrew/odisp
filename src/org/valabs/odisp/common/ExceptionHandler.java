package com.novel.odisp.common;

/** Интерфейс обработчиков ошибок.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ExceptionHandler.java,v 1.2 2004/08/18 12:48:39 valeks Exp $
 */
public interface ExceptionHandler {
	void signalException(Exception e);
}
