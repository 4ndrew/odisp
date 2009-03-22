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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.valabs.odisp.common.Dispatcher;
import org.valabs.odisp.common.Resource;
import org.valabs.odisp.common.ResourceManager;

/** Менеджер ресурсов.
 * @author (С) 2006 <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>
 * @version $Id: ResourceManager5.java,v 1.2 2006/03/29 11:33:02 valeks Exp $
 */
class ResourceManager5 implements ResourceManager {
  /** Ссылка на диспетчер объектов. */
  private final Dispatcher dispatcher;
  /** Словарь ресурсов (название -> дескриптор ресурса). */
  private final Map<String, ResourceEntry> resources = new HashMap<String, ResourceEntry>();

  /** Журнал. */
  private static final Logger log = Logger.getLogger(ResourceManager.class.getName());

  /** Доступ к ресурсам.
   * @return список ресурсов
   */
  public final Map<String, ResourceEntry> getResources() {
    return resources;
  }

  /** Динамическая загрузка ресурсных объектов.
   * @param className имя загружаемого класса
   * @param mult количество загружаемых объектов
   * @param config конфигурация ресурса
   */
  public final void loadResource(final String className, final int mult, final Map config) {
    int realMult = mult;
    StringBuffer logMessage = new StringBuffer();
    logMessage.append(realMult).append(" loading resource ");
    if (mult == ResourceEntry.MULT_SHARE) {
      logMessage.append("shared ");
      realMult = 1;
    }
    final ResourceEntry re = new ResourceEntry(mult, className);
    logMessage.append(className);
    for (int i = 0; i < realMult; i++) {
      try {
        final Resource r = (Resource) Class.forName(className).newInstance();
        r.setConfiguration(config);
        re.addResource(r);
        logMessage.append("+");
        synchronized (resources) {
          resources.put(className, re);
        }
        logMessage.append(" ok");
        log.config(logMessage.toString());
      } catch (Exception e) {
        dispatcher.getExceptionHandler().signalException(e);
      }
    }
  }

  /** Конструктор менеджера ресурсов.
   * @param newDispatcher ссылка на диспетчер ресурсами которого управляет менеджер
   */
  public ResourceManager5(final Dispatcher newDispatcher) {
    log.setLevel(java.util.logging.Level.ALL);
    dispatcher = newDispatcher;
  }

  public List<String> statRequest() {
    final List<String> result = new ArrayList<String>(resources.size());
    for (ResourceEntry re : resources.values()) {
      result.add(re.toString());
    }
    return result;
  }


  /**
   * @see org.valabs.odisp.common.ResourceManager#resourceAcquire(java.lang.String)
   */
  public final Resource resourceAcquire(final String className) {
    Resource result = null;
    while (result == null) {
      result = resourceTryAcquire(className);
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        dispatcher.getExceptionHandler().signalException(e);
      }
    }
    return result;
  }

  /**
   * @see org.valabs.odisp.common.ResourceManager#resourceTryAcquire(java.lang.String)
   */
  public Resource resourceTryAcquire(final String className) {
    Resource result = null;
    if (resources.containsKey(className)) {
      final ResourceEntry re = resources.get(className);
      synchronized (re) {
        if (re.isAvailable()) {
          result = re.acquireResource();
        }
      }
    }
    return result;
  }

  /**
   * @see org.valabs.odisp.common.ResourceManager#releaseResource(java.lang.String,
   *      org.valabs.odisp.common.Resource)
   */
  public void releaseResource(final String className, final Resource resource) {
    if (resources.containsKey(className)) {
      final ResourceEntry re = resources.get(className);
      synchronized (re) {
        re.releaseResource(resource);
      }
    }
  }
}
