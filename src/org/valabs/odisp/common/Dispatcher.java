package org.valabs.odisp.common;

import java.util.List;

import org.doomdark.uuid.UUID;

/** Стандартный интерфейс диспетчера, который должен реализовывать
 * класс, который выступает в качестве ODISP диспетчера.
 * @author (C) 2003 <a href="mailto:valeks@novel-il.ru">Валентин А. Алексеев</a>
 * @version $Id: Dispatcher.java,v 1.17 2005/02/27 12:37:29 valeks Exp $
 */
public interface Dispatcher {
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
			String origin, UUID inReplyTo);
  /** Создать пустое сообщение.
   * @return ссылка на сообщение
   */
  Message getNewMessage();

  /** Доступ к менеджеру объектов. */
  ObjectManager getObjectManager();
  /** Доступ к менеджеру ресурсов. */
  ResourceManager getResourceManager();
  /** Доступ к менеджеру безопасности. */
  SecurityManager getSecurityManager();
  /** Доступ к менеджеру конфигурации. */
  ConfigurationManager getConfigurationManager();
  /** Доступ к обработчику исключений. */
  ExceptionHandler getExceptionHandler();
  /** Добавление менеджера безопасности. */
  void addSecurityManager(SecurityManager additionalSecurityManager);
  /** Установка нового обработчика исключений. */
  void addExceptionHandler(ExceptionHandler exception);
  /** Добавление менеджера конфигурации. */
  void addConfigurationManager(ConfigurationManager cman);
}
