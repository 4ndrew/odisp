package org.valabs.odisp.common;

import java.util.Map;

/** Базовый класс реализующий почтовый ящик для сообщений
* посылаемых диспетчером ODISP.
* @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">Валентин А. Алексеев</a>
* @version $Id: ODObject.java,v 1.23 2005/01/26 22:17:49 valeks Exp $
*/
public interface ODObject {
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
  public void handleMessage(final Message msg);
  public void handleMessage0(final Message msg);

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

  /** 
   * Получать ли все сообщения в системе.
   */
  public boolean getMatchAll();
  
  /** Экспортировать состояние объекта для сохранение при перезагрузке.
   * @return данные, которые необходимо сохранить
   */
  public Map exportState();
  
  /** Импортировать состояние объекта после перезагрузки.
   * @param oldState старое значение состояния
   */
  public void importState(Map oldState);
}
