package com.novel.odisp.common;

import java.util.Map;

/** Интерфейс менеджера объектов ODISP.
 * @author (C) 2004 <a href="mailto:valeks@valeks.novel.local">Valentin A. Alekseev</a>
 * @version $Id: ObjectManager.java,v 1.2 2004/02/13 15:16:03 valeks Exp $
 */

public interface ObjectManager {
  /** Доступ к списку объектов.
   * @return список объектов
   */
  Map getObjects();
  /** Загрузка объекта.
   * @param name имя класса
   */
  void loadObject(String name);
  /** Выгрузка объекта.
   * @param objectName имя объекта
   * @param code код выхода
   */
  void unloadObject(String objectName, int code);
  /** Подгрузка заблокированных объектов. */
  void loadPending();
  /** Отслыка сообщения.
   * @param m сообщение
   */
  void send(Message m);
  /** Получение состояния блокировки объекта.
   * @param objName имя объекта
   * @return состояние блокировки
   */
  int getBlockedState(String objName);
  /** Установка состояния блокировки объекта.
   * @param objName имя объекта
   * @param newState новое состояние
   */
  void setBlockedState(String objName, int newState);
} // ObjectManager
