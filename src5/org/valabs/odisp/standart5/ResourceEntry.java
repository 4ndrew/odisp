package org.valabs.odisp.standart5;

import org.valabs.odisp.common.Resource;

/** Запись об однотипных ресурах в таблице ресурсов.
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ResourceEntry.java,v 1.2 2006/03/29 11:33:02 valeks Exp $
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
    for (ResourceItem ri : resourceStorage) {
      if (ri != null && !ri.isUsed()) {
        result = ri;
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
    for (ResourceItem  ri: resourceStorage) {
      if (ri != null && ri.getResource() == res) {
        result = ri;
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
    for (ResourceItem ri: resourceStorage) {
      ri.getResource().cleanUp(code);
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

    /** Ссылка на ресурс. */
    private final Resource resource;
    /** Установка ресурса.
     * @param newResource ссылка на ресурс
     */
    public ResourceItem(final Resource newResource) {
      resource = newResource;
    }
    /** Получение ссылки на ресурс.
     * @return ссылка на ресурс
     */
    public final Resource getResource() {
      return resource;
    }
  } // ResourceItem
} // ResourceEntry
