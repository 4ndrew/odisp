package com.novel.odisp;


import com.novel.odisp.common.ODObject;
/** Запись об объекте в таблице объектов.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ObjectEntry.java,v 1.1 2004/02/13 00:11:29 valeks Exp $
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
  /** Состояние блокировки. */
  private int blockedState;
  /** Вернуть состояние блокировки объекта.
   * @return состояние блокировки
   */
  public final int getBlockedState() {
    return blockedState;
  }
  /** Проверка заблокирован объект или нет.
   * @return статус блокировки
   */
  public final boolean isBlockedState() {
    return (blockedState > 0);
  }
  /** Установить состояние блокировки.
   * @param newBlockedState новое состояние блокировки
   */
  public final void setBlockedState(final int newBlockedState) {
    if (newBlockedState < 0) {
      blockedState = 0;
    } else {
      blockedState = newBlockedState;
    }
  }
  /** Ссылка на объект. */
  private ODObject object;
  /** Вернуть ссылку на объект.
   * @return ссылка на объект
   */
  public final ODObject getObject() {
    return object;
  }

  public final void setObject(ODObject newObject) {
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
  /** Убрать определенную зависимость из списка.
   * @param toRemove зависимость
   */
  public final void removeDepend(final String toRemove) {
    String[] newDeps = new String[depends.length - 1];
    for (int i = 0; i < depends.length; i++) {
      if (!depends[i].equals(toRemove)) {
	newDeps[i] = new String(depends[i]);
      }
    }
    depends = newDeps;
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
		     final int bs,
		     final String[] newDepends,
		     final String[] newProvides) {
    className = cn;
    blockedState = bs;
    depends = newDepends;
    provides = newProvides;
  }
} // ObjectEntry
