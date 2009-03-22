package org.valabs.odisp.common;

import java.util.List;

import org.doomdark.uuid.UUID;

/** Стандартный интерфейс диспетчера, который должен реализовывать
 * класс, который выступает в качестве ODISP диспетчера.
 * @author (C) 2003 <a href="mailto:valeks@novel-il.ru">Валентин А. Алексеев</a>
 * @version $Id: Dispatcher.java,v 1.18 2005/07/22 15:32:02 valeks Exp $
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

  /** Доступ к менеджеру объектов. 
   * @return ссылка на менеджер объектов
   */
  ObjectManager getObjectManager();
  /** Доступ к менеджеру ресурсов. 
   * @return ссылка на менеджер ресурсов
   */
  ResourceManager getResourceManager();
  /** Доступ к менеджеру безопасности.
   * @return ссылка на менеджер безопасности
   */
  SecurityManager getSecurityManager();
  /** Доступ к менеджеру конфигурации.
   * @return ссылка на менеджер конфигураций
   */
  ConfigurationManager getConfigurationManager();
  /** Доступ к обработчику исключений. 
   * @return ссылка на обработчик исключений
   */
  ExceptionHandler getExceptionHandler();
  /** Добавление менеджера безопасности. 
   * @param additionalSecurityManager новый менеджер безопасности
   */
  void addSecurityManager(SecurityManager additionalSecurityManager);
  /** Установка нового обработчика исключений.
   * @param exception новый обработчик исключений
   */
  void addExceptionHandler(ExceptionHandler exception);
  /** Добавление менеджера конфигурации. 
   * @param cman новый менеджер конфигураций
   */
  void addConfigurationManager(ConfigurationManager cman);
}
