package org.valabs.odisp.common;

import java.util.Map;

/** Базовый класс реализующий почтовый ящик для сообщений
* посылаемых диспетчером ODISP.
* @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">Валентин А. Алексеев</a>
* @version $Id: ODObject.java,v 1.25 2005/07/22 15:32:02 valeks Exp $
*/
public interface ODObject {
  /** Возвращает внутреннее ODISP имя объекта.
   * @return ODISP имя объекта
   */
  String getObjectName();
  /** Устанавливает диспетчера для текущего объекта.
   * @param newDisp диспетчер работающий с этим объектом
   */
  void setDispatcher(final Dispatcher newDisp);
  /** Интерфейс добавления сообщения в ящик.
   * @param msg сообщение для добавления
   */
  void handleMessage(final Message msg);
  void handleMessage0(final Message msg);

  /** Метод вызываемый для очистки данных класса.
   * @param type условие выхода
   * @return код возврата
   */
  int cleanUp(final int type);

  /** Выдача списка зависимостей.
   * @return массив зависимостей
   */
  String[] getDepends();
  /** Список сервисов предоставляемых объектом.
   * @return список сервисов
   */
  String[] getProviding();

  /** Установить таблицу параметров.
   * @param cfg новая таблица
   */
  void setConfiguration(final Map cfg);

  /** 
   * Получать ли все сообщения в системе.
   * @return true -- разрешить получение всех сообщений в системе
   */
  boolean getMatchAll();
  
  /** Экспортировать состояние объекта для сохранение при перезагрузке.
   * @return данные, которые необходимо сохранить
   */
  Map exportState();
  
  /** Импортировать состояние объекта после перезагрузки.
   * @param oldState старое значение состояния
   */
  void importState(Map oldState);
}
