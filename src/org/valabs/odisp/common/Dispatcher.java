package com.novel.odisp.common;

import java.util.List;

/** Стандартный интерфейс диспетчера, который должен реализовывать
 * класс, который выступает в качестве ODISP диспетчера.
 * @author Валентин А. Алексеев
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: Dispatcher.java,v 1.8 2004/02/13 12:11:43 valeks Exp $
 */
public interface Dispatcher {
  /** Посылка одиночного сообщения.
   * @param message сообщение
   * @deprecated необходимо использовать send(Message)
   */
  void sendMessage(Message message);
  /** Посылка списка сообщений.
   * @param messageList список сообщений
   * @deprecated необходимо использовать send(Message[])
   */
  void sendMessages(Message[] messageList);
  /** Посылка одиночного сообщения.
   * @param message сообщение
   */
  void send(Message message);
  /** Посылка списка сообщений.
   * @param messageList список сообщений
   */
  void send(Message[] messageList);
  /** Посылка списка сообщений.
   * @param messageList список сообщений
   */
  void send(List messageList);


  /** Генерация нового сообщения с заданными параметрами.
   * @param action действие
   * @param destination получатель
   * @param origin отправитель
   * @param inReplyTo сообщение на которое производится ответ
   * @return сообщение
   */
  Message getNewMessage(String action, String destination,
			String origin, int inReplyTo);
  /** Создать пустое сообщение.
   * @return ссылка на сообщение
   */
  Message getNewMessage();

  ObjectManager getObjectManager();
  ResourceManager getResourceManager();
}
