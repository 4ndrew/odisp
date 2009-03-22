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

/** ������ �� ������� � ������� ��������.
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ObjectEntry.java,v 1.3 2006/03/29 11:29:59 valeks Exp $
 */
class ObjectEntry {
	/** ���������� �������� �� ������. */
	private boolean loaded = false;
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
	private final Set<String> depends;
	/** ������� ������ ������������.
	 * @return ������ ������������
	 */
	public final Set<String> getDepends() {
		return depends;
	}
  
  private final Set<String> weakDepends;
  
  public final Set<String> getWeakDepends() {
    return weakDepends;
  }

	/** ������ ������������ ����������� �� ������.
	 * @param toRemove �����������
	 */
	public final void removeDepend(final String toRemove) {
	  depends.remove(toRemove);
	}

	/** ������ ��������. */
	private final Set<String> provides;
	/** ������� ������ ��������.
	 * @return ������ ��������
	 */
	public final Set<String> getProvides() {
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
