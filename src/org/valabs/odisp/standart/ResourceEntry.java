package com.novel.odisp;

import com.novel.odisp.common.Resource;
/** ������ � ������� � ������� ��������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ResourceEntry.java,v 1.1 2004/02/13 00:11:29 valeks Exp $
 */
public class ResourceEntry {
  /** ��������� ��������. */
  private boolean loaded;
  /** ������� ��������� ��������.
   * @return ��������� ��������
   */
  public final boolean isLoaded() {
    return loaded;
  }
  /** ���������� ��������� ��������.
   * @param newLoaded ����� �������� ���������
   */
  public final void setLoaded(final boolean newLoaded) {
    this.loaded = newLoaded;
  }

  /** ��� ������ �������. */
  private String className;
  /** ������ �� ������. */
  private Resource resource;
  /** ������� ������ �� ������.
   * @return ������ �� ������
   */
  public final Resource getResource() {
    return resource;
  }
  /** ���������� ������ �� ������.
   * @param newResource ����� �������� ������
   */
  public final void setResource(final Resource newResource) {
    resource = newResource;
  }
  /** ����������� ������.
   * @param cn ��� ������ �������
   */
  public ResourceEntry(final String cn) {
    loaded = false;
    className = cn;
  }
} // ResourceEntry
