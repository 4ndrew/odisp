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
 *//* ODISP -- Message Oriented Middleware
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.doomdark.uuid.UUID;
import org.valabs.odisp.common.ConfigurationManager;
import org.valabs.odisp.common.ExceptionHandler;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.ObjectManager;
import org.valabs.odisp.common.ResourceManager;
import org.valabs.odisp.common.SecurityManager;
import org.valabs.stdmsg.StandartMessage;

/**
 * Стандартный диспетчер ODISP.
 * Предоставляет интерфейс для отсылки сообщений.
 * 
 * @author (C) 2003-2005 <a href="mailto:valeks@novel-il.ru">Валентин А. Алексеев</a>
 * @author (C) 2003-2005 <a href="mailto:dron@novel-il.ru">Андрей А. Порохин</a>
 * @version $Id: Dispatcher.java,v 1.66 2006/03/14 13:31:51 valeks Exp $
 */
public class Dispatcher implements org.valabs.odisp.common.Dispatcher, ExceptionHandler {
  /** Журнал. */
  private static final Logger log = Logger.getLogger(Dispatcher.class.getName());
  /** Менеджер ресурсов. */
  private final ResourceManager rman = new org.valabs.odisp.standart.ResourceManager(this);
  /** Менеджер объектов. */
  private final ObjectManager oman = new org.valabs.odisp.standart.ObjectManager(this);
  /** Список менеджеров конфигурации. */
  private final ConfigurationManager cman = new org.valabs.odisp.standart.ConfigurationManager();
  /** Менеджер безопасности. */
  private SecurityManager sman;
  /** Обработчик исключений. */
  private ExceptionHandler ehandler;

  /**
   * Доступ к менеджеру объектов.
   *  
   * @return ссылка на менеджер объектов.
   */
  public final ObjectManager getObjectManager() {
    return oman;
  }

  /**
   * Доступ к менеджеру ресурсов.
   *  
   * @return ссылка на менеджер ресурсов.
   */
  public final ResourceManager getResourceManager() {
    return rman;
  }

  /**
   * Интерфейс для объектов ядра для отсылки сообщений.
   * 
   * @param message сообщение для посылки.
   */
  public final void send(final Message message) {
    oman.send(message);
  }

  /**
   * Интерфейс для объектов ядра для отсылки сообщений.
   * 
   * @param messageList список сообщений для посылки.
   */
  public final void send(final Message[] messageList) {
    if (messageList == null || messageList.length == 0) {
      return;
    }
    for (int i = 0; i < messageList.length; i++) {
      Message message = messageList[i];
      send(message);
    }
  }

  /**
   * Интерфейс для объектов ядра для отсылки сообщений.
   * 
   * @param messageList список сообщений для посылки.
   */
  public final void send(final List messageList) {
    if (messageList != null) {
      Iterator it = messageList.iterator();
      while (it.hasNext()) {
        send((Message) it.next());
      }
    }
  }

  /**
   * Интерфейс создания нового сообщения для сокрытия конкретной реализации
   * сообщений.
   * 
   * @param action действие которое несет сообщение.
   * @param destination адресат сообщения.
   * @param origin отправитель сообщения.
   * @param inReplyTo идентификатор сообщения на которое производится ответ.
   * @return Message созданное сообщение.
   */
  public final Message getNewMessage(final String action,
				     final String destination,
				     final String origin,
				     final UUID inReplyTo) {
    return new StandartMessage(action, destination, origin, inReplyTo);
  }

  /**
   * Создать новое пустое сообщение.
   * 
   * @return ссылка на сообщение.
   */
  public final Message getNewMessage() {
    return new StandartMessage();
  }

  /**
   * Конструктор. 
   *
   * @param args Аргументы.
   */
  public Dispatcher(List args) {
    log.info(toString() + " starting up...");
    
    Thread aliveThread = new Thread("alive thread") {
      public final void run() {
        try {
          synchronized (this) {
            wait();
          }
        } catch (InterruptedException e) { /* игнорируется. */ }
      }
    };
    Map tmp = new HashMap();
    tmp.put("runthr", aliveThread);
    oman.loadObject(DispatcherHandler.class.getName(), tmp);
    
    cman.setCommandLineArguments(args);
    if (getConfigurationManager().supportComponentListing()) {
      List resources = getConfigurationManager().getResourceList();
      List objects = getConfigurationManager().getObjectList();
      Iterator it = resources.iterator();
      while (it.hasNext()) {
        ConfigurationManager.ComponentConfiguration element = (ConfigurationManager.ComponentConfiguration) it.next();
        int mult = -1;
        if (element.getConfiguration() != null && element.getConfiguration().containsKey("mult")) {
          mult = new Integer((String) (element.getConfiguration().get("mult"))).intValue();
        }
        rman.loadResource(element.getClassName(), mult, element.getConfiguration());
      }
      it = objects.iterator();
      while (it.hasNext()) {
        ConfigurationManager.ComponentConfiguration element = (ConfigurationManager.ComponentConfiguration) it.next();
        oman.loadObject(element.getClassName(), element.getConfiguration());
      }

      oman.loadPending();

      aliveThread.start();
      try {
        aliveThread.join();
      } catch (InterruptedException e) { /* игнорируется. */ }
    } else {
      log.severe("Default configuration manager does not support component listing. Bailing out.");
    }
  }
  
  /**
   * Выводит сообщение об ошибке в случае некорректных параметров.
   */
  public static void usage() {
    log.severe("Usage: java org.valabs.odisp.standart.Dispatcher <config>*");
    System.exit(0);
  }
  
  /**
   * Точка входа в StandartDispatcher.
   * 
   * @param args по 0 должно содержать имя файла с перечислением классов,
   * которые необходимо загрузить.
   */
  public static void main(final String[] args) {
    log.setLevel(java.util.logging.Level.ALL);
    if (args.length < 1) {
		usage();
    } else {
      new Dispatcher(Arrays.asList(args));
    }
  }
  
  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#addSecurityManager(org.valabs.odisp.common.SecurityManager)
   */
  public void addSecurityManager(final SecurityManager additionalSecurityManager) {
  		sman = additionalSecurityManager;
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#getSecurityManager()
   */
  public SecurityManager getSecurityManager() {
    assert sman != null : "security manager is null! no one registered ever";
    return sman;
  }
 
  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#addExceptionHandler(org.valabs.odisp.common.ExceptionHandler)
   */
  public void addExceptionHandler(final ExceptionHandler exHandler) {
    ehandler = exHandler;
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#getExceptionHandler()
   */
  public ExceptionHandler getExceptionHandler() {
  		if(ehandler == null) {
  		  return this;
  		}
  		return ehandler;
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.ExceptionHandler#signalException(java.lang.Exception)
   */
  public void signalException(final Exception exception) {
    System.err.println("========================================================");
    System.err.println("Exception caught with default exception handler:");
    System.err.println("Exception: " + exception.toString());
    System.err.println("Stack trace:");
    exception.printStackTrace(System.err);
    System.err.println("========================================================");
  }
  
  /** @deprecated нет необходимости в множественных менеджерах конфигурации */
  public void addConfigurationManager(final ConfigurationManager _cman) {
    ((MultiConfigurationManager) cman).addConfigurationManager(_cman);
  }

  public ConfigurationManager getConfigurationManager() {
    return cman;
  }
  
  /**
   * Мультиплексор менеджеров конфигурации.
   * @deprecated нет необходимости в нескольких менеджерах конфигурации.
   */
  class MultiConfigurationManager implements ConfigurationManager {
    private final List cman = new ArrayList();
    void addConfigurationManager(final ConfigurationManager _cman) {
      cman.add(_cman);
    }
    public List getObjectList() {
      final Iterator cmanIt = cman.iterator();
      List result = null;
      while (cmanIt.hasNext()) {
        final ConfigurationManager element = (ConfigurationManager) cmanIt.next();
        if (element.supportComponentListing()) {
          result = element.getObjectList();
          break;
        }
      }
      return result;
    }
    
    public String getParameter(final String domain, final String paramName) {
      final Iterator cmanIt = cman.iterator();
      String result = null;
      while (cmanIt.hasNext()) {
        final ConfigurationManager element = (ConfigurationManager) cmanIt.next();
        final String value = element.getParameter(domain, paramName);
        if (value != null) {
          result = value;
          break;
        }
      }
      return result;
    }
    public List getResourceList() {
      final Iterator cmanIt = cman.iterator();
      List result = null;
      while (cmanIt.hasNext()) {
        final ConfigurationManager element = (ConfigurationManager) cmanIt.next();
        if (element.supportComponentListing()) {
          result = element.getResourceList();
          break;
        }
      }
      return result;
    }
    
    public void setCommandLineArguments(final List args) {
      final Iterator cmanIt = cman.iterator();
      while (cmanIt.hasNext()) {
        ((ConfigurationManager) cmanIt.next()).setCommandLineArguments(args);
      }
    }
    
    public boolean supportComponentListing() {
      final Iterator cmanIt = cman.iterator();
      boolean result = false;
      while (cmanIt.hasNext()) {
        final ConfigurationManager element = (ConfigurationManager) cmanIt.next();
        if (element.supportComponentListing()) {
          result = true;
          break;
        }
      }
      return result;
    }
    public boolean supportParameterFetching() {
      final Iterator cmanIt = cman.iterator();
      boolean result = false;
      while (cmanIt.hasNext()) {
        final ConfigurationManager element = (ConfigurationManager) cmanIt.next();
        if (element.supportParameterFetching()) {
          result = true;
          break;
        }
      }
      return result;
    }
}
} // StandartDispatcher
