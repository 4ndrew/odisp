package com.novel.odisp.common;

import java.util.Map;

/** Интерфейс ресурсного объекта ODISP ядра.
* @author Валентин А. Алексеев
* @author (C) 2003, НПП "Новел-ИЛ"
* @version $Id: Resource.java,v 1.4 2004/07/12 12:31:00 valeks Exp $
*/
public interface Resource {
  /** Максимальное количество ссылок одновременно поддерживаемых объектом.
   * @return максимальное кол-во ссылок
   */
  int getMaxReferenceCount();

  /** Вызывается при необходимости очистить ресурсы.
   * @param type признак выхода
   * @return код возврата
   */
  int cleanUp(int type);
  /** Установить конфигурацию.
   * @param configuration конфигурация
   */
  void setConfiguration(Map configuration);
}
