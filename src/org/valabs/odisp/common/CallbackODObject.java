package com.novel.odisp.common;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.regex.Pattern;

/** Базовый класс реализующий почтовый ящик для сообщений
* посылаемых диспетчером ODISP и вызывающий обработчик сообщения по мере прихода.
* @author Валентин А. Алексеев
* @author (С) 2003, НПП "Новел-ИЛ"
* @version $Id: CallbackODObject.java,v 1.5 2003/11/15 19:00:00 valeks Exp $
*/
public abstract class CallbackODObject extends ODObject {
  /** Карта обработчиков сообщений */
  private Map handlers; 
  /** Признак вызова метода регистрации обработчиков*/
  private boolean handlersRegistred = false;
  /** Список сообщений пришедших до регистрации обработчиков */
  private List unhandledMessages = new LinkedList();
  /** Конструктор объекта с заданным именем
   * @param name имя объекта
   */
  public CallbackODObject(String name) {
    super(name);
    handlers = new HashMap();
  }
  /** Добавление нового обработчика событий
      @param message сообщение обрабатываемое обработчиком
      @param handler класс-обработчик сообщения	
  */
  protected void addHandler(String message, MessageHandler handler) {
    if (handlers.containsKey(message)) {
      return;
    }
    logger.finest("registered handler for " + message);
    handlers.put(message, handler);
  }
  /** Цикл обработки приходящих сообщений */
  public final void run() {
    registerHandlers();
    handlersRegistred = true;
    ListIterator it = unhandledMessages.listIterator();
    while (it.hasNext()) {
      handleMessage((Message) it.next());
    }
    unhandledMessages.clear();
    // we do not need message loop.
  }
  /** Интерфейс добавления сообщения в ящик 
   * @param msg сообщение
   */
  public final void addMessage(Message msg) {
    if (!Pattern.matches(match, msg.getDestination())) {
      return;
    }
    handleMessage(msg);
  }
  /** Обработка сообщения
   * @param msg сообщение для обработки
   */
  protected final void handleMessage(Message msg) {
    if (!handlersRegistred) {
      unhandledMessages.add(msg);
    }
    if (handlers.containsKey(msg.getAction())) {
      ((MessageHandler) handlers.get(msg.getAction())).messageReceived(msg);
    } else {
      logger.finer("there is no handler for message " + msg.getAction());
    }
  }
  /** Метод вызываемый для очистки данных класса 
   * @param type признак выхода
   * @return код возврата
  */
  public int cleanUp(int type) {
    return 0;
  }
  /** Наследующие классы должны реализовывать этот метод для регистрации обработчиков  */
  protected abstract void registerHandlers();
}
