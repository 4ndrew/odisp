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
package org.valabs.odisp.standart;

import org.valabs.odisp.common.Resource;

/** Запись об однотипных ресурах в таблице ресурсов.
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ResourceEntry.java,v 1.17 2005/11/25 15:27:35 valeks Exp $
 */
class ResourceEntry {
  /** Уникальное обозначение разделяемого ресурса. */
  public static final int MULT_SHARE = -1;
  /** Имя класса ресурса. */
  private String className;
  /** Хранилище ресурсных записей. */
  private final ResourceItem[] resourceStorage;
  /** Количество использованных экземпляров. */
  private int usage = 0;
  /** Максимальное количество доступных экземпляров. */
  private int maxUsage = 0;
  /** Счетчик захватов. */
  private int acquireCount = 0;

  /** Поиск первого неиспользуемого ресурса.
   * @return запись о ресурсе
   */
  private ResourceItem lookupFirstUnused() {
    ResourceItem result = null;
    for (int i = 0; i < resourceStorage.length; i++) {
      if (resourceStorage[i] != null && !resourceStorage[i].isUsed()) {
        result = resourceStorage[i];
        break;
      }
    }
    return result;
  }

  /** Поиск ресурсной записи по ресурсному объекту.
   * @return запись о ресурсе
   */
  private ResourceItem lookupResourceItemByResource(final Resource res) {
    ResourceItem result = null;
    for (int i = 0; i < resourceStorage.length; i++) {
      if (resourceStorage[i] != null && resourceStorage[i].getResource() == res) {
        result = resourceStorage[i];
        break;
      }
    }
    return result;
  }

  /** Проверка готовности ресурса.
   * @return true в случае если ресурс доступен для захвата
   */
  public final boolean isAvailable() {
    return usage != 0 || maxUsage == MULT_SHARE;
  }

  /** Запросить ресурс.
   * @param usedBy описание того, кто пользуется ресурсом
   * @return ссылка на ресурс
   */
  public final Resource acquireResource(final String usedBy) {
    assert isAvailable();
    ResourceItem rit = null;
    if (usage == MULT_SHARE) {
      rit = (ResourceItem) resourceStorage[0]; // HACK
    } else {
      rit = lookupFirstUnused();
      usage--;
      rit.setUsed(true);
      rit.setUsedBy(usedBy);
    }
    assert rit != null;
    acquireCount++;
    return rit.getResource();
  }

  /** Разрешить повторное использование ресурса.
   * @param newResource ссылка на ресурс
   */
  public final void releaseResource(final Resource newResource) {
    if (maxUsage != MULT_SHARE) {
      lookupResourceItemByResource(newResource).setUsed(false);
      usage++;
    }
  }

  /** Добавить новый ресурс в хранилище.
   * @param newResource ссылка на ресурс
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
    for (int i = 0; i < resourceStorage.length; i++) {
      resourceStorage[i].getResource().cleanUp(code);
    }
  }

  /** Конструктор класса.
   * @param cn имя класса ресурса
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

  /** Подготовка статистики по записи.
   * @return строковое представление
   */
  public String toString() {
    String result = "\nClass name: " + className + "\n";
    if (maxUsage == MULT_SHARE) {
      result += "Shared resource. Acquire times: " + acquireCount;
    } else {
      result += "Usage: " + (maxUsage - usage) + " of " + maxUsage + ". Acquire times: " + acquireCount + "\n";
      result += "Usage map: ";
      for (int i = 0; i < resourceStorage.length; i++) {
        if (resourceStorage[i] != null) {
          result += resourceStorage[i].isUsed() + (resourceStorage[i].isUsed() ? "(" + resourceStorage[i].getUsedBy() + ")" : "") + " ";
        }
      }
    }
    result+= "\n";
    return result;
  }

  /** Хранилище данных о конкретной ресурсе. */
  private class ResourceItem {
    /** Флаг использования. */
    private boolean used = false;
    /** Вернуть флаг использования.
     * @return флаг использования
     */
    public boolean isUsed() {
      return used;
    }
    /** Установить флаг использования.
     * @param newUsed флаг использования
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
    /** Ссылка на ресурс. */
    private Resource resource;
    /** Установка ресурса.
     * @param newResource ссылка на ресурс
     */
    public final void setResource(final Resource newResource) {
      resource = newResource;
    }
    /** Получение ссылки на ресурс.
     * @return ссылка на ресурс
     */
    public final Resource getResource() {
      return resource;
    }
    public ResourceItem(final Resource nresource) {
      resource = nresource;
    }
  } // ResourceItem
} // ResourceEntry
