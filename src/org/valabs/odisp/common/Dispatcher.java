package com.novel.odisp.common;

/** Стандартный интерфейс диспетчера, который должен реализовывать
 * класс, который выступает в качестве ODISP диспетчера.
 * @author Валентин А. Алексеев
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: Dispatcher.java,v 1.4 2003/11/15 19:04:24 valeks Exp $
 */
public interface Dispatcher {
  /** Посылка одиночного сообщения 
   * @param message сообщение
   */
  void sendMessage(Message message);
  /** Посылка списка сообщений
   * @param messageList список сообщений
   */
  void sendMessages(Message[] messageList);
  /** Генерация нового сообщения с заданными параметрами
   * @param action действие
   * @param destination получатель
   * @param origin отправитель
   * @param inReplyTo сообщение на которое производится ответ
   * @return сообщение
   */
  Message getNewMessage(String action, String destination, String origin, int inReplyTo);
}
