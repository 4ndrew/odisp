package com.novel.odisp.common;

import java.util.List;
import java.util.Map;

/** Интерфейс менеджера ресурсов ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ResourceManager.java,v 1.7 2004/08/18 12:48:40 valeks Exp $
 */

public interface ResourceManager {
  /** Загрузка ресурса.
   * @param className имя класса
   * @param mult множитель
   */
  void loadResource(String className, int mult, Map params);
  /** Выгрузка ресурса.
   * @param name имя класса
   * @param code код выхода
   */
  void unloadResource(String name, int code);
  /** Получить список ресурсов.
   * @return карта ресурсов
   */
  Map getResources();
  /** Запрос на захват ресурса.
   * @param msg сообщение о захвате
   */
  void acquireRequest(Message msg);
  /** Запрос на высвобождение ресурса.
   * @param msg сообщение о захвате
   */
  void releaseRequest(Message msg);
  /** Вернуть статистику по ресурсам.
   * @return Список строк.
   */
  List statRequest();
} // ResourceManager
