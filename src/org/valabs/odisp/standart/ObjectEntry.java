package com.novel.odisp;

import java.util.Arrays;

import com.novel.odisp.common.ODObject;
/** Запись об объекте в таблице объектов.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ObjectEntry.java,v 1.6 2004/05/21 21:49:28 valeks Exp $
 */

public class ObjectEntry {
  /** Определяет загружен ли объект. */
  private boolean loaded;
  /** Проверка загрузки объекта.
   * @return состояние загрузки
   */
  public final boolean isLoaded() {
    return loaded;
  }

  /** Установка признака загрузки.
   * @param newLoaded новое значение состояния
   */
  public final void setLoaded(final boolean newLoaded) {
    loaded = newLoaded;
  }

  /** Имя класса для объекта. */
  private String className;
  /** Вернуть имя класса для объекта.
   * @return имя класса
   */
  public final String getClassName() {
    return className;
  }

  /** Установить имя класса для объекта.
   * @param newClassName новое имя класса
   */
  public final void setClassName(final String newClassName) {
    className = newClassName;
  }

  /** Ссылка на объект. */
  private ODObject object;
  /** Вернуть ссылку на объект.
   * @return ссылка на объект
   */
  public final ODObject getObject() {
    return object;
  }

  /** Установка объекта.
   * @param newObject новый объекта
   */
  public final void setObject(final ODObject newObject) {
    object = newObject;
  }

  /** Список зависимостей. */
  private String[] depends;
  /** Вернуть список зависимостей.
   * @return список зависимостей
   */
  public final String[] getDepends() {
    return depends;
  }

  /** Поиск зависимости в списке.
   * @return номер в списке или -1 если элемент отсутствует
   */
  private int depContains(final String depName) {
    return Arrays.asList(depends).indexOf(depName);
  }

  /** Убрать определенную зависимость из списка.
   * @param toRemove зависимость
   */
  public final void removeDepend(final String toRemove) {
    if (depContains(toRemove) > 0) {
      String[] newDeps = new String[depends.length - 1];
      int count = 0;
      for (int i = 0; i < depends.length; i++) {
	if (!depends[i].equals(toRemove)) {
	  newDeps[count++] = depends[i];
	}
      }
      depends = newDeps;
    }
  }
  
  /** Список сервисов. */
  private String[] provides;
  /** Вернуть список сервисов.
   * @return список сервисов
   */
  public final String[] getProvides() {
    return provides;
  }

  /** Конструктор класса.
   * @param cn имя класса
   * @param bs изначальное состояние блокировки
   * @param newDepends список зависимостей
   * @param newProvides список сервисов
   */
  public ObjectEntry(final String cn,
		     final String[] newDepends,
		     final String[] newProvides) {
    className = cn;
    depends = newDepends;
    provides = newProvides;
  }
} // ObjectEntry
