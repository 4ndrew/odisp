package com.novel.odisp;

import com.novel.odisp.common.Resource;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/** ������ �� ���������� ������� � ������� ��������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ResourceEntry.java,v 1.2 2004/02/13 22:22:50 valeks Exp $
 */
public class ResourceEntry {
  /** ��� ������ �������. */
  private String className;
  /** ��������� ��������� �������. */
  private List resourceStorage = new ArrayList();
  /** ���������� �������������� �����������. */
  private int usage = 0;

  /** ����� ������� ��������������� �������.
   * @return ������ � �������
   */
  private ResourceItem lookupFirstByUse(boolean use) {
    Iterator it = resourceStorage.iterator();
    while (it.hasNext()) {
      ResourceItem rit = (ResourceItem) it.next();
      if (rit.isUsed() == use) {
	return rit;
      }
    }
    assert true;
    return null; // never reached in case of error.
  }

  /** ����� ������� ��������������� �������.
   * @return ������ � �������
   */
  private ResourceItem lookupFirstUnused() {
    return lookupFirstByUse(false);
  }

  /** ����� ������� ������������� �������.
   * @return ������ � �������
   */
  private ResourceItem lookupFirstUsed() {
    return lookupFirstByUse(true);
  }

  /** ����� ��������� ������ �� ���������� �������.
   * @return ������ � �������
   */
  private ResourceItem lookupResourceItemByResource(final Resource res) {
    Iterator it = resourceStorage.iterator();
    while (it.hasNext()) {
      ResourceItem rit = (ResourceItem) it.next();
      if(rit.getResource().equals(res)) {
	return rit;
      }
    }
    assert true;
    return null; // never reached in case of error.
  }

  /** �������� ������� ���������� ������� ������������ ������.
   * @param res ������ �� ������
   * @return ��������� ����������
   */
  public final boolean isBlockState(final Resource res) {
    return lookupResourceItemByResource(res).isBlockState();
  }

  /** ��������� ��������� ������������ ������� ��� ������� ������� �������.
   * @param res ������ �� ������
   * @param blockedState ������ ����������
   */
  public final void setBlockState(final Resource res, final boolean blockedState) {
    lookupResourceItemByResource(res).setBlockState(blockedState);
  }

  /** ����� ������� �� �������.
   * @param idx ������ �������
   */
  private ResourceItem lookupResourceItemByIdx(int idx) {
    assert idx > resourceStorage.size();
    return (ResourceItem) resourceStorage.get(idx);
  }

  /** ��������� ������ ��������� �������������� �����������. */
  public final void setMaxUsage(final int newUsage) {
    usage = newUsage;
  }

  public final boolean isAvailable() {
    return usage != 0;
  }

  /** ��������� ������.
   * @return ������ �� ������
   */
  public final Resource acquireResource() {
    assert usage == 0;
    usage--;
    ResourceItem rit = lookupFirstUnused();
    rit.setUsed(true);
    return rit.getResource();
  }

  /** ��������� ��������� ������������� �������.
   * @param newResource ������ �� ������
   */
  public final void releaseResource(final Resource newResource) {
    ResourceItem rit = lookupResourceItemByResource(newResource);
    rit.setUsed(false);
    usage++;
  }

  /** �������� ����� ������ � ���������.
   * @param newResource ������ �� ������
   */
  public final void addResource(final Resource newResource) {
    ResourceItem it = new ResourceItem();
    it.setUsed(false);
    it.setResource(newResource);
    resourceStorage.add(it);
  }

  public final void cleanUp(final int code) {
    Iterator it = resourceStorage.iterator();
    while (it.hasNext()) {
      ResourceItem rit = (ResourceItem) it.next();
      rit.getResource().cleanUp(code);
    }
  }

  /** ����������� ������.
   * @param cn ��� ������ �������
   */
  public ResourceEntry(final String cn) {
    className = cn;
  }

  /** ��������� ������ � ���������� �������. */
  private class ResourceItem {
    /** ���� �������������. */
    private boolean used = false;
    /** ������� ���� �������������.
     * @return ���� �������������
     */
    public boolean isUsed() {
      return used;
    }
    /** ���������� ���� �������������.
     * @param newUsed ���� �������������
     */
    public final void setUsed(final boolean newUsed) {
      used = newUsed;
    }
    /** ���� ����������. */
    private boolean blockState = false;
    /** ������� ���� ����������.
     * @return ���� ����������
     */
    public boolean isBlockState() {
      return blockState;
    }
    /** ���������� ���� ����������.
     * @param newUsed ���� ����������
     */
    public final void setBlockState(final boolean newBlockState) {
      blockState = newBlockState;
    }
    /** ������ �� ������. */
    private Resource resource;
    /** ��������� �������.
     * @param newResource ������ �� ������
     */
    public final void setResource(final Resource newResource) {
      resource = newResource;
    }
    /** ��������� ������ �� ������.
     * @return ������ �� ������
     */
    public final Resource getResource() {
      return resource;
    }
  } // ResourceItem
} // ResourceEntry
