package com.novel.odisp;

import com.novel.odisp.common.Resource;
/** Запись о ресурсе в таблице ресурсов.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ResourceEntry.java,v 1.1 2004/02/13 00:11:29 valeks Exp $
 */
public class ResourceEntry {
  /** Состояние загрузки. */
  private boolean loaded;
  /** Вернуть состояние загрузки.
   * @return состояние загрузки
   */
  public final boolean isLoaded() {
    return loaded;
  }
  /** Установить состояние загрузки.
   * @param newLoaded новое значение состояния
   */
  public final void setLoaded(final boolean newLoaded) {
    this.loaded = newLoaded;
  }

  /** Имя класса ресурса. */
  private String className;
  /** Ссылка на ресурс. */
  private Resource resource;
  /** Вернуть ссылку на ресурс.
   * @return ссылка на ресурс
   */
  public final Resource getResource() {
    return resource;
  }
  /** Установить ссылку на ресурс.
   * @param newResource новое значение ссылки
   */
  public final void setResource(final Resource newResource) {
    resource = newResource;
  }
  /** Конструктор класса.
   * @param cn имя класса ресурса
   */
  public ResourceEntry(final String cn) {
    loaded = false;
    className = cn;
  }
} // ResourceEntry
