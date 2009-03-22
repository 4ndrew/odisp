package org.valabs.odisp.standart;

import java.util.Set;
import java.util.TreeSet;

import org.valabs.odisp.common.ODObject;
import org.valabs.odisp.common.WeakDependency;

/** Запись об объекте в таблице объектов.
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ObjectEntry.java,v 1.17 2006/03/29 11:17:29 valeks Exp $
 */
class ObjectEntry {
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
	private final ODObject object;
	/** Вернуть ссылку на объект.
	 * @return ссылка на объект
	 */
	public final ODObject getObject() {
		return object;
	}

	/** Список зависимостей. */
	private final Set depends;
	/** Вернуть список зависимостей.
	 * @return список зависимостей
	 */
	public final Set getDepends() {
		return depends;
	}
  
  private final Set weakDepends;
  
  public final Set getWeakDepends() {
    return weakDepends;
  }

	/** Убрать определенную зависимость из списка.
	 * @param toRemove зависимость
	 */
	public final void removeDepend(final String toRemove) {
	  depends.remove(toRemove);
	}

	/** Список сервисов. */
	private final Set provides;
	/** Вернуть список сервисов.
	 * @return список сервисов
	 */
	public final Set getProvides() {
		return provides;
	}

	/** Конструктор класса.
	 * @param _className имя класса
	 * @param newDepends список зависимостей
	 * @param newProvides список сервисов
	 */
	public ObjectEntry(final String _className, final ODObject _obj) {
		className = _className;
    object = _obj;
    depends = new TreeSet();
    weakDepends = new TreeSet();
    String[] newDepends = object.getDepends();
    for (int i = 0; i < newDepends.length; i++) {
      if (WeakDependency.isWeakDependency(newDepends[i])) {
        weakDepends.add(newDepends[i]);
      } else {
        depends.add(newDepends[i]);
      }
    }

    provides = new TreeSet();
    String[] newProvides = object.getProviding();
    for (int i = 0; i < newProvides.length; i++) {
      provides.add(newProvides[i]);
    }
	}

	private boolean intoHints = true;

	/**
	 * @return Returns the intoHints.
	 */
	public boolean isIntoHints() {
		return intoHints;
	}
	/**
	 * @param intoHints The intoHints to set.
	 */
	public void setIntoHints(final boolean intoHints) {
		this.intoHints = intoHints;
	}
} // ObjectEntry
