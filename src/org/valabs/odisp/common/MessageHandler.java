package com.novel.odisp.common;

import com.novel.odisp.common.*;
/** Интерфейс обработчика сообщения ядра ODISP системы
 * @author Валентин А. Алексеев
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: MessageHandler.java,v 1.1 2003/10/07 11:06:47 valeks Exp $
 */
public interface MessageHandler {
    /** Метод вызывается при получении базовым классом сообщения зарегистрированного типа */
    public void messageReceived(Message msg);
}
