package com.novel.odisp.common;

import java.util.Map;

/** Интерфейс менеджера ресурсов ODISP.
 * @author (C) 2004 <a href="mailto:valeks@valeks.novel.local">Valentin A. Alekseev</a>
 * @version $Id: ResourceManager.java,v 1.3 2004/02/13 15:16:03 valeks Exp $
 */

public interface ResourceManager {
  /** Загрузка ресурса.
   * @param className имя класса
   * @param mult множитель
   * @param param параметр запуска
   */
  void loadResource(String className, int mult, String param);
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
} // ResourceManager
