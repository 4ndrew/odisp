package com.novel.odisp.common;

import java.util.List;
import java.util.Map;

/** Интерфейс менеджера объектов ODISP.
 * @author (C) 2004 <a href="mailto:valeks@valeks.novel.local">Valentin A. Alekseev</a>
 * @version $Id: ObjectManager.java,v 1.6 2004/05/21 21:49:28 valeks Exp $
 */

public interface ObjectManager {
  /** Доступ к списку объектов.
   * @return список объектов
   */
  Map getObjects();
  /** Загрузка объекта.
   * @param name имя класса
   * @param params параметры загрузки
   */
  void loadObject(String name, Map params);
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
  /** Получить список сервисов.
   * @return немодифицируемый список сервисов
   */
  List getProviding();
  /** Удалить провайдера из списка.
   * @param service название сервиса
   * @param objectName название объекта
   */
  void removeProvider(String service, String objectName);
  /** Добавить провайдера в список.
   * @param service название сервиса
   * @param objectName название объекта
   */
  void addProvider(String service, String objectName);
} // ObjectManager
