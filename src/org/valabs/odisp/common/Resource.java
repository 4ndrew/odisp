package com.novel.odisp.common;

/** Интерфейс ресурсного объекта ODISP ядра.
* @author Валентин А. Алексеев
* @author (C) 2003, НПП "Новел-ИЛ"
* @version $Id: Resource.java,v 1.2 2004/01/16 10:29:15 valeks Exp $
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
}
