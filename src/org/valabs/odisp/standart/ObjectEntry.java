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
