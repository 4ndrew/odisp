package com.novel.odisp.common;

/** Интерфейс обработчика сообщения ядра ODISP системы.
 * @author (C) <a href="mailto:valeks@novel-il.ru">Валентин А. Алексеев</a>
 * @version $Id: MessageHandler.java,v 1.4 2004/08/18 12:48:39 valeks Exp $
 */
public interface MessageHandler {
  /** Метод вызывается при получении базовым классом сообщения.
   * зарегистрированного типа.
   * @param msg полученное сообщение
   */
  void messageReceived(Message msg);
}
