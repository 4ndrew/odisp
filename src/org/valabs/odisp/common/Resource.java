package org.valabs.odisp.common;

import java.util.Map;

/** Интерфейс ресурсного объекта ODISP ядра.
* @author (C) 2003, <a href="mailto:valeks@novel-il.ru">Валентин А. Алексеев</a>
* @version $Id: Resource.java,v 1.6 2004/08/23 07:42:36 valeks Exp $
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
