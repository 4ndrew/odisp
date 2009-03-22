package org.valabs.odisp.common;

/** Интерфейс обработчика сообщения ядра ODISP системы.
 * @author (C) <a href="mailto:valeks@novel-il.ru">Валентин А. Алексеев</a>
 * @version $Id: MessageHandler.java,v 1.5 2004/08/23 07:42:37 valeks Exp $
 */
public interface MessageHandler {
  /** Метод вызывается при получении базовым классом сообщения.
   * зарегистрированного типа.
   * @param msg полученное сообщение
   */
  void messageReceived(Message msg);
}
