package com.novel.odisp.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Pattern;

/** Базовый класс реализующий почтовый ящик для сообщений
* посылаемых диспетчером ODISP и вызывающий обработчик сообщения
* по мере прихода.
* @author Валентин А. Алексеев
* @author (С) 2003, НПП "Новел-ИЛ"
* @version $Id: CallbackODObject.java,v 1.14 2004/05/11 09:55:39 valeks Exp $
*/
public abstract class CallbackODObject extends ODObject {
  /** Карта обработчиков сообщений. */
  private Map handlers;
  /** Признак вызова метода регистрации обработчиков. */
  private boolean handlersRegistred = false;
  /** Список сообщений пришедших до регистрации обработчиков. */
  private List unhandledMessages = new LinkedList();
  /** Конструктор объекта с заданным именем.
   * @param name имя объекта
   */
  public CallbackODObject(final String name) {
    super(name);
    handlers = new HashMap();
  }
  /** Добавление нового обработчика событий.
      @param message сообщение обрабатываемое обработчиком
      @param handler класс-обработчик сообщения
  */
  protected final void addHandler(final String message,
				  final MessageHandler handler) {
    if (handlers.containsKey(message)) {
      return;
    }
    logger.finest("registered handler for " + message);
    handlers.put(message, handler);
  }

  /** Интерфейс добавления сообщения в ящик.
   * @param msg сообщение
   */
  public final void addMessage(final Message msg) {
    if (!Pattern.matches(match, msg.getDestination())
	&& !Pattern.matches(msg.getDestination(), getObjectName())) {
      return;
    }
    handleMessage(msg);
  }
  /** Обработка сообщения.
   * @param msg сообщение для обработки
   */
  protected final void handleMessage(final Message msg) {
    if (!handlersRegistred) {
      registerHandlers();
    }
    if (handlers.containsKey(msg.getAction())) {
      ((MessageHandler) handlers.get(msg.getAction())).messageReceived(msg);
    } else {
      logger.finer("there is no handler for message " + msg.getAction());
    }
  }
  /** Метод вызываемый для очистки данных класса.
   * @param type признак выхода
   * @return код возврата
  */
  public int cleanUp(final int type) {
    return 0;
  }
  /** Наследующие классы должны реализовывать этот метод
   * для регистрации обработчиков. */
  protected abstract void registerHandlers();
}
