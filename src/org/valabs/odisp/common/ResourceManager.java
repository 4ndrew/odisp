package com.novel.odisp.common;

import java.util.Map;
import com.novel.odisp.common.Resource;

/** Интерфейс менеджера ресурсов ODISP.
 * @author (C) 2004 <a href="mailto:valeks@valeks.novel.local">Valentin A. Alekseev</a>
 * @version $Id: ResourceManager.java,v 1.2 2004/02/13 13:15:17 valeks Exp $
 */

public interface ResourceManager {
  /** Загрузка ресурса.
   * @param className имя класса
   * @param mult множитель
   * @param param параметр запуска
   */
  public void loadResource(String className, int mult, String param);
  /** Выгрузка ресурса.
   * @param name имя класса
   * @param code код выхода
   */
  public void unloadResource(String name, int code);
  /** Получить список ресурсов.
   * @return карта ресурсов
   */
  public Map getResources();
  /** Запрос на захват ресурса.
   * @param msg сообщение о захвате
   */
  public void acquireRequest(Message msg);
  /** Запрос на высвобождение ресурса.
   * @param msg сообщение о захвате
   */
  public void releaseRequest(Message msg);
}// ResourceManager
