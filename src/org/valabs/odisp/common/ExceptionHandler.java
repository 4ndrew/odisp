package com.novel.odisp.common;

/** Интерфейс обработчиков ошибок.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004 НПП "Новел-ИЛ"
 * @version $Id: ExceptionHandler.java,v 1.1 2004/07/12 09:57:04 valeks Exp $
 */
public interface ExceptionHandler {
	void signalException(Exception e);
}
