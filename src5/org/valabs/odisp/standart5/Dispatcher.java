/*
 * This is a part of odisp.
 * See LICENSE for licensing details.
 */
package org.valabs.odisp.standart5;

import java.util.Arrays;
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

/** Диспетчер ODISP для Java 1.5.
 * @author (C) <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>, 2005
 * @version $Id: Dispatcher.java,v 1.5 2006/03/29 11:33:24 valeks Exp $
 */
public final class Dispatcher implements org.valabs.odisp.common.Dispatcher, ExceptionHandler {
  /** Журнал. */
  private static final Logger log = Logger.getLogger(Dispatcher.class.getName());
  /** Менеджер безопасности. */
  private SecurityManager sman;
  /** Обработчик исключений. */
  private ExceptionHandler ehandler = this;
  /** Менеджер ресурсов. */
  private ResourceManager5 rman = new ResourceManager5(this);
  /** Менеджер конфигураций. */
  private ConfigurationManager5 cman = new ConfigurationManager5();
  /** Менеджер объектов. */
  private ObjectManager5 oman = new ObjectManager5(this);

  /**
   * @see org.valabs.odisp.common.Dispatcher#send(org.valabs.odisp.common.Message)
   */
  public void send(Message message) {
    oman.send(message);
  }

  /**
   * @see org.valabs.odisp.common.Dispatcher#send(org.valabs.odisp.common.Message[])
   */
  public void send(Message[] messageList) {
    if (messageList == null || messageList.length == 0) {
      return;
    }
    for (Message message: messageList) {
      send(message);
    }
  }

  /**
   * @see org.valabs.odisp.common.Dispatcher#send(java.util.List)
   */
  public void send(List messageList) {
    if (messageList != null) {
      Iterator it = messageList.iterator();
      while (it.hasNext()) {
        send((Message) it.next());
      }
    }
  }

  /**
   * @see org.valabs.odisp.common.Dispatcher#getNewMessage(String, String, String, UUID)
   */
  public Message getNewMessage(String action, String destination, String origin, UUID inReplyTo) {
    return new StandartMessage(action, destination, origin, inReplyTo);
  }

  /**
   * @see org.valabs.odisp.common.Dispatcher#getNewMessage()
   */
  public Message getNewMessage() {
    return new StandartMessage();
  }

  /**
   * @see org.valabs.odisp.common.Dispatcher#getObjectManager()
   */
  public ObjectManager getObjectManager() {
    return oman;
  }

  /**
   * @see org.valabs.odisp.common.Dispatcher#getResourceManager()
   */
  public ResourceManager getResourceManager() {
    return rman;
  }

  /**
   * @see org.valabs.odisp.common.Dispatcher#getSecurityManager()
   */
  public SecurityManager getSecurityManager() {
    return sman;
  }

  /**
   * @see org.valabs.odisp.common.Dispatcher#getConfigurationManager()
   */
  public ConfigurationManager getConfigurationManager() {
    return cman;
  }

  /**
   * @see org.valabs.odisp.common.Dispatcher#getExceptionHandler()
   */
  public ExceptionHandler getExceptionHandler() {
    return ehandler;
  }

  /**
   * @see org.valabs.odisp.common.Dispatcher#addSecurityManager(org.valabs.odisp.common.SecurityManager)
   */
  public void addSecurityManager(SecurityManager additionalSecurityManager) {
    sman = additionalSecurityManager;
  }

  /**
   * @see org.valabs.odisp.common.Dispatcher#addExceptionHandler(org.valabs.odisp.common.ExceptionHandler)
   */
  public void addExceptionHandler(ExceptionHandler exception) {
    ehandler = exception;
  }

  /**
   * @deprecated нет необходимости во множестве менеджеров конфигурации
   * @see org.valabs.odisp.common.Dispatcher#addConfigurationManager(org.valabs.odisp.common.ConfigurationManager)
   */
  public void addConfigurationManager(ConfigurationManager _cman) {
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    log.setLevel(java.util.logging.Level.ALL);
    if (args.length < 1) {
      log.severe("Usage: java org.valabs.odisp.standart.Dispatcher <config>");
      System.exit(0);
    } else {
      new Dispatcher(Arrays.asList(args));
    }
  }
  
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
    Map<String, Object> tmp = new HashMap<String, Object>();
    tmp.put("runthr", aliveThread);
    oman.loadObject(DispatcherHandler5.class.getName(), tmp);
    
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
  
  public void signalException(final Exception exception) {
    System.err.println("========================================================");
    System.err.println("Exception caught with default exception handler:");
    System.err.println("Exception: " + exception.toString());
    System.err.println("Stack trace:");
    exception.printStackTrace(System.err);
    System.err.println("========================================================");
  }
}
