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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.valabs.odisp.common.Dispatcher;
import org.valabs.odisp.common.Resource;

/** Менеджер ресурсных объектов ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ResourceManager.java,v 1.41 2005/11/25 15:27:35 valeks Exp $
 */
class ResourceManager implements org.valabs.odisp.common.ResourceManager {
  /** Ссылка на диспетчер объектов. */
  private final Dispatcher dispatcher;
  /** Нить обработки запросов. */
  private final Map resources = new HashMap();

  /** Журнал. */
  private static final Logger log = Logger.getLogger(ResourceManager.class.getName());

  /** Доступ к ресурсам.
   * @return список ресурсов
   */
  public final Map getResources() {
    return resources;
  }

  /** Динамическая загрузка ресурсных объектов.
   * @param className имя загружаемого класса
   * @param mult количество загружаемых объектов
   * @param config конфигурация ресурса
   */
  public final void loadResource(final String className, final int mult, final Map config) {
    int realMult = mult;
    String logMessage = realMult + " loading resource ";
    if (mult == ResourceEntry.MULT_SHARE) {
      logMessage += "shared ";
      realMult = 1;
    }
    final ResourceEntry re = new ResourceEntry(mult, className);
    logMessage += className;
    for (int i = 0; i < realMult; i++) {
      try {
        final Resource r = (Resource) Class.forName(className).newInstance();
        r.setConfiguration(config);
        re.addResource(r);
        logMessage += "+";
        synchronized (resources) {
          resources.put(className, re);
        }
        logMessage += " ok";
        log.config(logMessage);
      } catch (ClassNotFoundException e) {
        log.warning(" failed: " + e);
      } catch (InstantiationException e) {
        log.warning(" failed: " + e);
      } catch (IllegalAccessException e) {
        log.warning(" failed: " + e);
      }
    }
  }

  /** Конструктор менеджера ресурсов.
   * @param newDispatcher ссылка на диспетчер ресурсами которого управляет менеджер
   */
  public ResourceManager(final Dispatcher newDispatcher) {
    log.setLevel(java.util.logging.Level.ALL);
    dispatcher = newDispatcher;
  }

  public List statRequest() {
    final List result = new ArrayList();
    Iterator it = resources.keySet().iterator();
    while (it.hasNext()) {
      result.add(resources.get(it.next()).toString());
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
    synchronized (resources) {
      if (resources.containsKey(className)) {
        final ResourceEntry re = (ResourceEntry) resources.get(className);
        if (re.isAvailable()) {
          result = re.acquireResource("whom-ever"); // XXX
        }
      }
    }
    return result;
  }

  /**
   * @see org.valabs.odisp.common.ResourceManager#releaseResource(java.lang.String, org.valabs.odisp.common.Resource)
   */
  public void releaseResource(final String className, final Resource resource) {
    synchronized (resources) {
      if (resources.containsKey(className)) {
        ((ResourceEntry) resources.get(className)).releaseResource(resource);
      }
    }
  }
} // StandartResourceManager
