package com.novel.odisp.common;

import java.util.Map;

/** Базовый класс реализующий почтовый ящик для сообщений
* посылаемых диспетчером ODISP.
* @author Валентин А. Алексеев
* @author (С) 2003, НПП "Новел-ИЛ"
* @version $Id: ODObject.java,v 1.17 2004/05/13 09:22:37 valeks Exp $
*/
public interface ODObject /* extends Thread */ {
  /** Возвращает внутреннее ODISP имя объекта.
   * @return ODISP имя объекта
   */
  public String getObjectName();
  /** Устанавливает диспетчера для текущего объекта.
   * @param d диспетчер работающий с этим объектом
   */
  public void setDispatcher(final Dispatcher d);
  /** Интерфейс добавления сообщения в ящик.
   * @param msg сообщение для добавления
   */
  public void addMessage(final Message msg);

  /** Метод вызываемый для очистки данных класса.
   * @param type условие выхода
   * @return код возврата
   */
  public int cleanUp(final int type);

  /** Выдача списка зависимостей.
   * @return массив зависимостей
   */
  public String[] getDepends();
  /** Список сервисов предоставляемых объектом.
   * @return список сервисов
   */
  public String[] getProviding();

  /** Установить таблицу параметров.
   * @param cfg новая таблица
   */
  public void setConfiguration(final Map cfg);
}
