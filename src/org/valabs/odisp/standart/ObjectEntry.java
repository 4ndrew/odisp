package org.valabs.odisp.standart;

import java.util.Arrays;

import org.valabs.odisp.common.ODObject;

/** ������ �� ������� � ������� ��������.
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ObjectEntry.java,v 1.10 2004/08/23 07:42:37 valeks Exp $
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
  private String[] depends;
  /** ������� ������ ������������.
   * @return ������ ������������
   */
  public final String[] getDepends() {
    return depends;
  }

  /** ����� ����������� � ������.
   * @return ����� � ������ ��� -1 ���� ������� �����������
   */
  private int depContains(final String depName) {
    return Arrays.asList(depends).indexOf(depName);
  }

  /** ������ ������������ ����������� �� ������.
   * @param toRemove �����������
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
  
  /** ������ ��������. */
  private String[] provides;
  /** ������� ������ ��������.
   * @return ������ ��������
   */
  public final String[] getProvides() {
    return provides;
  }

  /** ����������� ������.
   * @param cn ��� ������
   * @param newDepends ������ ������������
   * @param newProvides ������ ��������
   */
  public ObjectEntry(final String cn,
		     final String[] newDepends,
		     final String[] newProvides) {
    className = cn;
    depends = newDepends;
    provides = newProvides;
  }
} // ObjectEntry
