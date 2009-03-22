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
package org.valabs.odisp.standart;

import java.util.Set;
import java.util.TreeSet;

import org.valabs.odisp.common.ODObject;
import org.valabs.odisp.common.WeakDependency;

/** ������ �� ������� � ������� ��������.
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ObjectEntry.java,v 1.17 2006/03/29 11:17:29 valeks Exp $
 */
class ObjectEntry {
	/** ���������� �������� �� ������. */
	private boolean loaded;
	/** �������� �������� �������.
	 * @return ��������� ��������
	 */
	public final boolean isLoaded() {
		return loaded;
	}

	/** ��������� �������� ��������.
	 * @param newLoaded ����� �������� ���������
	 */
	public final void setLoaded(final boolean newLoaded) {
		loaded = newLoaded;
	}

	/** ��� ������ ��� �������. */
	private String className;
	/** ������� ��� ������ ��� �������.
	 * @return ��� ������
	 */
	public final String getClassName() {
		return className;
	}

	/** ���������� ��� ������ ��� �������.
	 * @param newClassName ����� ��� ������
	 */
	public final void setClassName(final String newClassName) {
		className = newClassName;
	}

	/** ������ �� ������. */
	private final ODObject object;
	/** ������� ������ �� ������.
	 * @return ������ �� ������
	 */
	public final ODObject getObject() {
		return object;
	}

	/** ������ ������������. */
	private final Set depends;
	/** ������� ������ ������������.
	 * @return ������ ������������
	 */
	public final Set getDepends() {
		return depends;
	}
  
  private final Set weakDepends;
  
  public final Set getWeakDepends() {
    return weakDepends;
  }

	/** ������ ������������ ����������� �� ������.
	 * @param toRemove �����������
	 */
	public final void removeDepend(final String toRemove) {
	  depends.remove(toRemove);
	}

	/** ������ ��������. */
	private final Set provides;
	/** ������� ������ ��������.
	 * @return ������ ��������
	 */
	public final Set getProvides() {
		return provides;
	}

	/** ����������� ������.
	 * @param _className ��� ������
	 * @param newDepends ������ ������������
	 * @param newProvides ������ ��������
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
