package org.valabs.odisp.standart;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.valabs.odisp.common.ODObject;

/** ������ �� ������� � ������� ��������.
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ObjectEntry.java,v 1.12 2005/02/12 17:27:29 valeks Exp $
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
	private Set depends;
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
	private Set provides;
	/** ������� ������ ��������.
	 * @return ������ ��������
	 */
	public final Set getProvides() {
		return provides;
	}

	/** ����������� ������.
	 * @param cn ��� ������
	 * @param newDepends ������ ������������
	 * @param newProvides ������ ��������
	 */
	public ObjectEntry(final String cn, final String[] newDepends,
			final String[] newProvides) {
		className = cn;
		depends = new HashSet(Arrays.asList(newDepends));
		provides = new HashSet(Arrays.asList(newProvides));
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
	public void setIntoHints(boolean intoHints) {
		this.intoHints = intoHints;
	}
} // ObjectEntry
