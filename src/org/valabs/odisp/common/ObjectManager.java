package com.novel.odisp.common;

import java.util.Map;

/** Интерфейс менеджера объектов ODISP.
 * @author (C) 2004 <a href="mailto:valeks@valeks.novel.local">Valentin A. Alekseev</a>
 * @version $Id: ObjectManager.java,v 1.1 2004/02/13 12:11:43 valeks Exp $
 */

public interface ObjectManager {
  /** Доступ к списку объектов. */
  Map getObjects();
  /** Загрузка объекта. */
  void loadObject(String name);
  /** Выгрузка объекта. */
  void unloadObject(String objectName, int code);
  /** Подгрузка заблокированных объектов. */
  void loadPending();
  /** Отслыка сообщения. */
  void send(Message m);
  /** Получение состояния блокировки объекта. */
  int getBlockedState(String objName);
  /** Установка состояния блокировки объекта. */
  void setBlockedState(String objName, int newState);
}// ObjectManager
