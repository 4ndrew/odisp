package com.novel.odisp.common;

/** Интерфейс обработчика сообщения ядра ODISP системы
 * @author Валентин А. Алексеев
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: MessageHandler.java,v 1.2 2003/11/15 19:08:18 valeks Exp $
 */
public interface MessageHandler {
  /** Метод вызывается при получении базовым классом сообщения зарегистрированного типа 
   * @param msg полученное сообщение
   */
  void messageReceived(Message msg);
}
