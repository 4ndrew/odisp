package com.novel.odisp;

import java.util.ArrayList;
import java.util.List;

import com.novel.odisp.common.ODObject;
/** ������ �� ������� � ������� ��������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ObjectEntry.java,v 1.3 2004/03/31 12:00:21 dron Exp $
 */

public class ObjectEntry {
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

  /** ��������� ����������. */
  private int blockedState;
  /** ������� ��������� ���������� �������.
   * @return ��������� ����������
   */
  public final int getBlockedState() {
    return blockedState;
  }

  /** �������� ������������ ������ ��� ���.
   * @return ������ ����������
   */
  public final boolean isBlockedState() {
    return (blockedState > 0);
  }

  /** ���������� ��������� ����������.
   * @param newBlockedState ����� ��������� ����������
   */
  public final void setBlockedState(final int newBlockedState) {
    if (newBlockedState < 0) {
      blockedState = 0;
    } else {
      blockedState = newBlockedState;
    }
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

  /** ������ ������������ ����������� �� ������.
   * @param toRemove �����������
   */
  public final void removeDepend(final String toRemove) {
    List newDeps = new ArrayList();
    for (int i = 0; i < depends.length; i++) {
      if (!depends[i].equals(toRemove)) {
	newDeps.add(new String(depends[i]));
      }
    }
    depends = (String []) newDeps.toArray();
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
   * @param bs ����������� ��������� ����������
   * @param newDepends ������ ������������
   * @param newProvides ������ ��������
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
