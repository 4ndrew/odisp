package com.novel.odisp.standart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.novel.odisp.common.Resource;

/** ������ �� ���������� ������� � ������� ��������.
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ResourceEntry.java,v 1.11 2004/08/18 12:48:40 valeks Exp $
 */
class ResourceEntry {
  /** ������. */
  private static final Logger log = Logger.getLogger("ResourceEntry");
  /** ���������� ����������� ������������ �������. */
  public static final int MULT_SHARE = -1;
  /** ��� ������ �������. */
  private String className;
  /** ��������� ��������� �������. */
  private List resourceStorage = new ArrayList();
  /** ���������� �������������� �����������. */
  private int usage = 0;
  /** ������������ ���������� ��������� �����������. */
  private int maxUsage = 0;
  /** ������� ��������. */
  private int acquireCount = 0;

  /** ����� ������� ��������������� �������.
   * @return ������ � �������
   */
  private ResourceItem lookupFirstUnused() {
    Iterator it = resourceStorage.iterator();
    while (it.hasNext()) {
      ResourceItem rit = (ResourceItem) it.next();
      if (!rit.isUsed()) {
	return rit;
      }
    }
    assert false : "asked to lookup for free resource " + className + " but found nothing";
    return null; // never reached in case of error.
  }

  /** ����� ��������� ������ �� ���������� �������.
   * @return ������ � �������
   */
  private ResourceItem lookupResourceItemByResource(final Resource res) {
    Iterator it = resourceStorage.iterator();
    while (it.hasNext()) {
      ResourceItem rit = (ResourceItem) it.next();
      if(rit.getResource() == res) {
	return rit;
      }
    }
    assert false : "asked to lookup for resource item by resource but i found nothing";
    return null; // never reached in case of error.
  }

  /** ��������� ������ ��������� �������������� �����������. */
  public final void setMaxUsage(final int newUsage) {
    usage = newUsage;
    maxUsage = newUsage;
    if (newUsage == MULT_SHARE) {
      log.fine(className + " marked as shared resource.");
    }
  }

  /** �������� ���������� �������.
   * @return true � ������ ���� ������ �������� ��� �������
   */
  public final boolean isAvailable() {
    return usage != 0 || maxUsage == MULT_SHARE;
  }

  /** ��������� ������.
   * @return ������ �� ������
   */
  public final Resource acquireResource(String usedBy) {
    assert isAvailable();
    ResourceItem rit = null;
    if (usage != MULT_SHARE) {
      rit = lookupFirstUnused();
      usage--;
      rit.setUsed(true);
      rit.setUsedBy(usedBy);
    } else {
      rit = (ResourceItem) resourceStorage.get(0); // HACK
    }
    assert rit != null;
    acquireCount++;
    return rit.getResource();
  }

  /** ��������� ��������� ������������� �������.
   * @param newResource ������ �� ������
   */
  public final void releaseResource(final Resource newResource) {
    if (maxUsage != MULT_SHARE) {
      ResourceItem rit = lookupResourceItemByResource(newResource);
      rit.setUsed(false);
      usage++;
    }
  }

  /** �������� ����� ������ � ���������.
   * @param newResource ������ �� ������
   */
  public final void addResource(final Resource newResource) {
    resourceStorage.add(new ResourceItem(newResource));
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
    public ResourceItem(final Resource nresource) {
      resource = nresource;
    }
  } // ResourceItem
} // ResourceEntry
