package com.novel.odisp.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/** Базовый класс реализующий почтовый ящик для сообщений
* посылаемых диспетчером ODISP.
* @author Валентин А. Алексеев
* @author (С) 2003, НПП "Новел-ИЛ"
* @version $Id: ODObject.java,v 1.16 2004/05/11 09:55:39 valeks Exp $
*/
public abstract class ODObject /* extends Thread */ {
  /** Журнал. */
  protected Logger logger;
  /** Диспетчер работающий с этим объектом. */
  protected Dispatcher dispatcher;
  /** Список сообщений к обработке. */
  protected List messages;
  /** Таблица конфигурационных параметров. */
  private Map configuration;
  /** Regex маска принимаемых сообщений.
   * По умолчанию инициализируется именем объекта. */
  protected String match;
  /** Внутреннее имя объекта в ядре ODISP. */
  public String name;
  /** Изменить маску принимаемых сообщений.
   * @param newMatch новая маска
   */
  protected final void setMatch(final String newMatch) {
    match = newMatch;
  }
  /** Конструктор инициализирующий почтовый ящик.
   * @param newName имя объекта
   */
  public ODObject(final String newName) {
    messages = new ArrayList();
    name = newName;
    match = newName;
    logger = Logger.getLogger(newName);
  }
  /** Доступ к диспетчеру.
   * @return ссылка на диспетчер
   */
  protected final Dispatcher getDispatcher() {
    return dispatcher;
  }
  /** Возвращает внутреннее ODISP имя объекта.
   * @return ODISP имя объекта
   */
  public final String getObjectName() {
    return name;
  }
  /** Устанавливает диспетчера для текущего объекта.
   * @param d диспетчер работающий с этим объектом
   */
  public final void setDispatcher(final Dispatcher d) {
    this.dispatcher = d;
  }
  /** Интерфейс добавления сообщения в ящик.
   * @param msg сообщение для добавления
   */
  public abstract void addMessage(final Message msg);

  /** Добавление списка сообщений в ящик.
   * @param newMessages список сообщений для добавления
   */
  public final void addMessages(final List newMessages) {
    Iterator it = newMessages.iterator();
    while (it.hasNext()) {
		addMessage((Message) it.next());
    }
  }
  /** Метод который вызывает для обработки пришедшего сообщения.
   * Подклассы обязаны реализовать этот метод для корректной работы.
   * @param msg сообщение для обработки
   */
  protected abstract void handleMessage(final Message msg);

  /** Метод вызываемый для очистки данных класса.
   * @param type условие выхода
   * @return код возврата
   */
  public abstract int cleanUp(final int type);
  /** Вывод журнальных сообщений на экран.
   * @param place участок кода к которому относится сообщение
   * @param msg сообщение
   * @deprecated необходимо использовать logger
   */
  protected final void log(final String place, final String msg) {
    logger.fine(getObjectName() + "." + place + ": " + msg);
  }
  /** Выдача списка зависимостей.
   * @return массив зависимостей
   */
  public abstract String[] getDepends();
  /** Список сервисов предоставляемых объектом.
   * @return список сервисов
   */
  public abstract String[] getProviding();
  /** Установить таблицу параметров.
   * @param cfg новая таблица
   */
  public final void setConfiguration(final Map cfg) {
    configuration = cfg;
  }
  /** Получить значение параметра конфигурации.
   * @param name имя параметра
   */
  protected final String getParameter(final String name) {
    if (configuration != null && configuration.containsKey(name)) {
      return (String) configuration.get(name);
    }
    return null;
  }
  /** Получить значение параметра конфигурации с учетом значения по-умолчанию.
   * @param name имя параметра
   * @param defValue значение по умолчанию
   */
  protected final String getParameter(final String name, final String defValue) {
    return getParameter(name) == null ? defValue : getParameter(name);
  }
}
