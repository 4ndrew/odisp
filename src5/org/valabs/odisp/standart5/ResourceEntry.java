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

import org.valabs.odisp.common.Resource;

/** ������ �� ���������� ������� � ������� ��������.
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ResourceEntry.java,v 1.2 2006/03/29 11:33:02 valeks Exp $
 */
class ResourceEntry {
  /** ���������� ����������� ������������ �������. */
  public static final int MULT_SHARE = -1;
  /** ��� ������ �������. */
  private String className;
  /** ��������� ��������� �������. */
  private final ResourceItem[] resourceStorage;
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
    ResourceItem result = null;
    for (ResourceItem ri : resourceStorage) {
      if (ri != null && !ri.isUsed()) {
        result = ri;
        break;
      }
    }
    return result;
  }

  /** ����� ��������� ������ �� ���������� �������.
   * @return ������ � �������
   */
  private ResourceItem lookupResourceItemByResource(final Resource res) {
    ResourceItem result = null;
    for (ResourceItem  ri: resourceStorage) {
      if (ri != null && ri.getResource() == res) {
        result = ri;
        break;
      }
    }
    return result;
  }

  /** �������� ���������� �������.
   * @return true � ������ ���� ������ �������� ��� �������
   */
  public final boolean isAvailable() {
    return usage != 0 || maxUsage == MULT_SHARE;
  }

  /** ��������� ������.
   * @param usedBy �������� ����, ��� ���������� ��������
   * @return ������ �� ������
   */
  public final Resource acquireResource() {
    assert isAvailable();
    ResourceItem rit = null;
    if (maxUsage == MULT_SHARE) {
      rit = resourceStorage[0]; // HACK
    } else {
      rit = lookupFirstUnused();
      usage--;
      rit.setUsed(true);
    }
    acquireCount++;
    return rit.getResource();
  }

  /** ��������� ��������� ������������� �������.
   * @param newResource ������ �� ������
   */
  public final void releaseResource(final Resource newResource) {
    if (maxUsage != MULT_SHARE) {
      lookupResourceItemByResource(newResource).setUsed(false);
      usage++;
    }
  }

  /** �������� ����� ������ � ���������.
   * @param newResource ������ �� ������
   */
  public final void addResource(final Resource newResource) {
    for (int i = 0; i < resourceStorage.length; i++) {
      if (resourceStorage[i] == null) {
        resourceStorage[i] = new ResourceItem(newResource);
        break;
      }
    }
  }

  public final void cleanUp(final int code) {
    for (ResourceItem ri: resourceStorage) {
      ri.getResource().cleanUp(code);
    }
  }

  /** ����������� ������.
   * @param cn ��� ������ �������
   */
  public ResourceEntry(final int mult, final String cn) {
    if (mult == MULT_SHARE) {
      maxUsage = MULT_SHARE;
      resourceStorage = new ResourceItem[1];
    } else {
      maxUsage = mult;
      resourceStorage = new ResourceItem[maxUsage];
    }
    className = cn;
  }

  /** ���������� ���������� �� ������.
   * @return ��������� �������������
   */
  public String toString() {
    StringBuffer result = new StringBuffer("\nClass name: ").append(className).append("\n");
    if (maxUsage == MULT_SHARE) {
      result.append("Shared resource. Acquire times: ").append(acquireCount);
    } else {
      result.append("Usage: ").append(maxUsage - usage).append(" of ").append(maxUsage).append(". Acquire times: ").append(acquireCount).append("\n");
      result.append("Usage map: ");
      for (ResourceItem ri: resourceStorage) {
        result.append(ri.isUsed());
        result.append(" ");
      }
    }
    result.append("\n");
    return result.toString();
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

    /** ������ �� ������. */
    private final Resource resource;
    /** ��������� �������.
     * @param newResource ������ �� ������
     */
    public ResourceItem(final Resource newResource) {
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
