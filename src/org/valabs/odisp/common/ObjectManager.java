package org.valabs.odisp.common;

import java.util.List;
import java.util.Map;

/** Интерфейс менеджера объектов ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ObjectManager.java,v 1.10 2005/03/03 08:47:50 valeks Exp $
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
   * @param message сообщение
   */
  void send(Message message);
  /** Получить список сервисов.
   * @return немодифицируемый список сервисов
   */
  List getProviding();
  /** Удалить провайдера из списка.
   * @param service название сервиса
   * @param objectName название объекта
   * @deprecated начиная с версии 1.17 StandartODObject'а нет возможности
   * динамически изменить список сервисов объекта, который хранится для проверки
   * совпадения destination сообщения и имени/сервисов предоставляемых объектом.
   */
  void removeProvider(String service, String objectName);
  /** Добавить провайдера в список.
   * @param service название сервиса
   * @param objectName название объекта
   * @deprecated начиная с версии 1.17 StandartODObject'а нет возможности
   * динамически изменить список сервисов объекта, который хранится для проверки
   * совпадения destination сообщения и имени/сервисов предоставляемых объектом.
   */
  void addProvider(String service, String objectName);
} // ObjectManager
