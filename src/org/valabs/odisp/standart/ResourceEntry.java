package com.novel.odisp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.novel.odisp.common.Resource;

/** ������ �� ���������� ������� � ������� ��������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ResourceEntry.java,v 1.8 2004/05/21 21:49:28 valeks Exp $
 */
public class ResourceEntry {
  /** ������. */
  private Logger log = Logger.getLogger("com.novel.odisp.ResourceEntry");
  /** ���������� ����������� ������������ �������. */
  public static final int MULT_SHARE = -1;
  /** ��� ������ �������. */
  private String className;
  /** ��������� ��������� �������. */
  private List resourceStorage = new ArrayList();
  /** ���������� �������������� �����������. */
  private int usage = 0;
  private int maxUsage = 0;
  private int acquireCount = 0;
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

  /** ����� ������� �� �������.
   * @param idx ������ �������
   */
  private ResourceItem lookupResourceItemByIdx(int idx) {
    assert idx <= resourceStorage.size();
    return (ResourceItem) resourceStorage.get(idx);
  }

  /** ��������� ������ ��������� �������������� �����������. */
  public final void setMaxUsage(final int newUsage) {
    usage = newUsage;
    maxUsage = newUsage;
    if (newUsage == MULT_SHARE) {
      log.fine(className + " marked as shared resource.");
    }
  }

  public final boolean isAvailable() {
    return usage != 0 || maxUsage == MULT_SHARE;
  }

  /** ��������� ������.
   * @return ������ �� ������
   */
  public final Resource acquireResource(String usedBy) {
    assert usage != 0;
    ResourceItem rit = lookupFirstUnused();
    if (usage != MULT_SHARE) {
      usage--;
      rit.setUsed(true);
    }
    rit.setUsedBy(usedBy);
    acquireCount++;
    return rit.getResource();
  }

  /** ��������� ��������� ������������� �������.
   * @param newResource ������ �� ������
   */
  public final void releaseResource(final Resource newResource) {
      ResourceItem rit = lookupResourceItemByResource(newResource);
      if (maxUsage != MULT_SHARE) {
	rit.setUsed(false);
	usage++;
      }
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

  /** ���������� ���������� �� ������.
   * @return ��������� �������������
   */
  public String toString() {
    String result = "\nClass name: " + className + "\n";
    if (maxUsage != MULT_SHARE) {
      result += "Usage: " + (maxUsage - usage) + " of " + maxUsage + ". Acquire times: " + acquireCount + "\n";
      result += "Usage map: ";
      Iterator it = resourceStorage.iterator();
      while (it.hasNext()) {
	ResourceItem rit = (ResourceItem) it.next();
	result+= rit.isUsed() + (rit.isUsed() ? "(" + rit.getUsedBy() + ")" : "" ) + " ";
      }
    } else {
      result += "Shared resource. Acquire times: " + acquireCount;
    }
    result+= "\n";
    return result;
  }

  public void setUsedBy(Resource res, String usedBy) {
    ResourceItem ri = lookupResourceItemByResource(res);
    acquireCount++;
    ri.setUsedBy(usedBy);
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

    private String usedBy = "";
    public String getUsedBy() {
      return usedBy;
    }
    public final void setUsedBy(String newUsedBy) {
      usedBy = newUsedBy;
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
