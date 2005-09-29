package org.valabs.odisp.standart;

import java.util.Set;
import java.util.TreeSet;

import org.valabs.odisp.common.ODObject;

/** ������ �� ������� � ������� ��������.
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ObjectEntry.java,v 1.15 2005/09/29 16:56:54 valeks Exp $
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
	private ODObject object;
	/** ������� ������ �� ������.
	 * @return ������ �� ������
	 */
	public final ODObject getObject() {
		return object;
	}

	/** ��������� �������.
	 * @param newObject ����� �������
	 */
	public final void setObject(final ODObject newObject) {
		object = newObject;
	}

	/** ������ ������������. */
	private final Set depends;
	/** ������� ������ ������������.
	 * @return ������ ������������
	 */
	public final Set getDepends() {
		return depends;
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
	public ObjectEntry(final String _className, final String[] newDepends,
			final String[] newProvides) {
		className = _className;
    depends = new TreeSet();
    for (int i = 0; i < newDepends.length; i++) {
      depends.add(newDepends[i]);
    }

    provides = new TreeSet();
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
