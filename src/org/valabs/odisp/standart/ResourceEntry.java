package com.novel.odisp;

import com.novel.odisp.common.Resource;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/** Запись об однотипных ресурах в таблице ресурсов.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ResourceEntry.java,v 1.2 2004/02/13 22:22:50 valeks Exp $
 */
public class ResourceEntry {
  /** Имя класса ресурса. */
  private String className;
  /** Хранилище ресурсных записей. */
  private List resourceStorage = new ArrayList();
  /** Количество использованных экземпляров. */
  private int usage = 0;

  /** Поиск первого неиспользуемого ресурса.
   * @return запись о ресурсе
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

  /** Поиск первого неиспользуемого ресурса.
   * @return запись о ресурсе
   */
  private ResourceItem lookupFirstUnused() {
    return lookupFirstByUse(false);
  }

  /** Поиск первого используемого ресурса.
   * @return запись о ресурсе
   */
  private ResourceItem lookupFirstUsed() {
    return lookupFirstByUse(true);
  }

  /** Поиск ресурсной записи по ресурсному объекту.
   * @return запись о ресурсе
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

  /** Проверка статуса блокировки объекта запросившего ресурс.
   * @param res ссылка на ресурс
   * @return состояние блокировки
   */
  public final boolean isBlockState(final Resource res) {
    return lookupResourceItemByResource(res).isBlockState();
  }

  /** Установка состояния блокирования объекта при захвате данного ресурса.
   * @param res ссылка на ресурс
   * @param blockedState статус блокировки
   */
  public final void setBlockState(final Resource res, final boolean blockedState) {
    lookupResourceItemByResource(res).setBlockState(blockedState);
  }

  /** Поиск ресурса по индексу.
   * @param idx индекс ресурса
   */
  private ResourceItem lookupResourceItemByIdx(int idx) {
    assert idx > resourceStorage.size();
    return (ResourceItem) resourceStorage.get(idx);
  }

  /** Установка нового количеств использованных экземпляров. */
  public final void setMaxUsage(final int newUsage) {
    usage = newUsage;
  }

  public final boolean isAvailable() {
    return usage != 0;
  }

  /** Запросить ресурс.
   * @return ссылка на ресурс
   */
  public final Resource acquireResource() {
    assert usage == 0;
    usage--;
    ResourceItem rit = lookupFirstUnused();
    rit.setUsed(true);
    return rit.getResource();
  }

  /** Разрешить повторное использование ресурса.
   * @param newResource ссылка на ресурс
   */
  public final void releaseResource(final Resource newResource) {
    ResourceItem rit = lookupResourceItemByResource(newResource);
    rit.setUsed(false);
    usage++;
  }

  /** Добавить новый ресурс в хранилище.
   * @param newResource ссылка на ресурс
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

  /** Конструктор класса.
   * @param cn имя класса ресурса
   */
  public ResourceEntry(final String cn) {
    className = cn;
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
    /** Флаг блокировки. */
    private boolean blockState = false;
    /** Вернуть флаг блокировки.
     * @return флаг блокировки
     */
    public boolean isBlockState() {
      return blockState;
    }
    /** Установить флаг блокировки.
     * @param newUsed флаг блокировки
     */
    public final void setBlockState(final boolean newBlockState) {
      blockState = newBlockState;
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
  } // ResourceItem
} // ResourceEntry
