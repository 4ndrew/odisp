/* ODISP -- Message Oriented Middleware
 * Copyright (C) 2003-2005 Valentin A. Alekseev
 * Copyright (C) 2003-2005 Andrew A. Porohin 
 * 
 * ODISP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.1 of the License.
 * 
 * ODISP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ODISP.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.valabs.odisp.standart5;

import java.util.Set;
import java.util.TreeSet;

import org.valabs.odisp.common.ODObject;
import org.valabs.odisp.common.WeakDependency;

/** Запись об объекте в таблице объектов.
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ObjectEntry.java,v 1.3 2006/03/29 11:29:59 valeks Exp $
 */
class ObjectEntry {
	/** Определяет загружен ли объект. */
	private boolean loaded = false;
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
	private final Set<String> depends;
	/** Вернуть список зависимостей.
	 * @return список зависимостей
	 */
	public final Set<String> getDepends() {
		return depends;
	}
  
  private final Set<String> weakDepends;
  
  public final Set<String> getWeakDepends() {
    return weakDepends;
  }

	/** Убрать определенную зависимость из списка.
	 * @param toRemove зависимость
	 */
	public final void removeDepend(final String toRemove) {
	  depends.remove(toRemove);
	}

	/** Список сервисов. */
	private final Set<String> provides;
	/** Вернуть список сервисов.
	 * @return список сервисов
	 */
	public final Set<String> getProvides() {
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
    depends = new TreeSet<String>();
    weakDepends = new TreeSet<String>();
    for (String dep : object.getDepends()) {
      if (WeakDependency.isWeakDependency(dep)) {
        weakDepends.add(dep);
      } else {
        depends.add(dep);
      }
    }

    provides = new TreeSet<String>();
    for (String prov : object.getProviding()) {
      provides.add(prov);
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
