package org.valabs.odisp.standart;

import java.util.ArrayList;
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

/** ����������� ��������� ODISP.
 * ����������� ��������� ��������� ��������� ��������� ����� ��������� ����
 * � ���������� ���������� ���������.
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
 * @author (C) 2003-2004 <a href="mailto:dron@novel-il.ru">������ �. �������</a>
 * @version $Id: Dispatcher.java,v 1.59 2005/01/27 14:15:43 valeks Exp $
 */
public class Dispatcher implements org.valabs.odisp.common.Dispatcher, ExceptionHandler {
  /** ������. */
  private static Logger log = Logger.getLogger(Dispatcher.class.getName());
  /** �������� ��������. */
  private ResourceManager rman = new org.valabs.odisp.standart.ResourceManager(this);
  /** �������� ��������. */
  private ObjectManager oman = new org.valabs.odisp.standart.ObjectManager(this);
  /** ������ ���������� ������������. */
  private ConfigurationManager cman = new MultiConfigurationManager();
  /** �������� ������������. */
  private SecurityManager sman = null;
  /** ���������� ����������. */
  private ExceptionHandler ehandler = null;

  /** ������ � ��������� ��������. 
   * @return ������ �� �������� ��������
   */
  public final ObjectManager getObjectManager() {
    return oman;
  }

  /** ������ � ��������� ��������. 
   * @return ������ �� �������� ��������
   */
  public final ResourceManager getResourceManager() {
    return rman;
  }

  /** ��������� ��� �������� ���� ��� ������� ���������.
   * @param message ��������� ��� �������
   */
  public final void send(final Message message) {
    oman.send(message);
  }

  /** ��������� ��� �������� ���� ��� ������� ���������.
   * @param messageList ������ ��������� ��� �������
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

  /** ��������� ��� �������� ���� ��� ������� ���������.
   * @param messageList ������ ��������� ��� �������
   */
  public final void send(final List messageList) {
    if (messageList != null) {
      Iterator it = messageList.iterator();
      while (it.hasNext()) {
        send((Message) it.next());
      }
    }
  }

  /** ��������� �������� ������ ��������� ��� �������� ���������� ����������
   * ���������.
   * @param action �������� ������� ����� ���������
   * @param destination ������� ���������
   * @param origin ����������� ���������
   * @param inReplyTo ������������� ��������� �� ������� ������������ �����
   * @return Message ��������� ���������
   */
  public final Message getNewMessage(final String action,
				     final String destination,
				     final String origin,
				     final UUID inReplyTo) {
    return new StandartMessage(action, destination, origin, inReplyTo);
  }

  /** ������� ����� ������ ���������.
   * @return ������ �� ���������
   */
  public final Message getNewMessage() {
    return new StandartMessage();
  }

  public Dispatcher(List args) {
    log.info(toString() + " starting up...");
    addConfigurationManager(new org.valabs.odisp.standart.ConfigurationManager());
    getConfigurationManager().setCommandLineArguments(args);
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
      Thread t = new Thread("alive thread") {
        public final void run() {
          try {
            synchronized (this) {
              wait();
            }
          } catch (InterruptedException e) {
          }
        }
      };
      Map tmp = new HashMap();
      tmp.put("runthr", t);
      oman.loadObject(DispatcherHandler.class.getName(), tmp);
      oman.loadPending();

      t.start();
      try {
        t.join();
      } catch (InterruptedException e) {

      }
    } else {
      log.severe("Default configuration manager does not support component listing. Bailing out.");
    }
  }
  
  /** ������� ��������� �� ������ � ������ ������������ ����������. */
  public static void usage() {
    log.severe("Usage: java org.valabs.odisp.standart.Dispatcher <config>");
    System.exit(0);
  }
  /** ����� ����� � StandartDispatcher.
   * @param args �� 0 ������ ��������� ��� ����� � ������������� �������,
   * ������� ���������� ���������
   */
  /*public static void main(final String[] args) {
    log.setLevel(java.util.logging.Level.ALL);
    if (args.length != 1) {
		usage();
    } else {
      new Dispatcher(args);
    }
  }*/
  
  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Dispatcher#addSecurityManager(org.valabs.odisp.common.SecurityManager)
   */
  public void addSecurityManager(SecurityManager additionalSecurityManager) {
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
  public void addExceptionHandler(ExceptionHandler ex) {
    ehandler = ex;
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
  public void signalException(Exception e) {
    System.err.println("========================================================");
    System.err.println("Exception caught with default exception handler:");
    System.err.println("Exception: " + e.toString());
    System.err.println("Stack trace:");
    e.printStackTrace(System.err);
    System.err.println("========================================================");
  }
  
  public void addConfigurationManager(ConfigurationManager _cman) {
    ((MultiConfigurationManager) cman).addConfigurationManager(_cman);
  }

  public ConfigurationManager getConfigurationManager() {
    return cman;
  }
  
  /** ������������� ���������� ������������. */
  class MultiConfigurationManager implements ConfigurationManager {
    List cman = new ArrayList();
    void addConfigurationManager(ConfigurationManager _cman) {
      cman.add(_cman);
    }
    public List getObjectList() {
      Iterator it = cman.iterator();
      while (it.hasNext()) {
        ConfigurationManager element = (ConfigurationManager) it.next();
        if (element.supportComponentListing()) {
          return element.getObjectList();
        }
      }
      return null;
    }
    
    public String getParameter(String domain, String paramName) {
      Iterator it = cman.iterator();
      while (it.hasNext()) {
        ConfigurationManager element = (ConfigurationManager) it.next();
        String value = element.getParameter(domain, paramName);
        if (value != null) {
          return value;
        }
      }
      return null;
    }
    public List getResourceList() {
      Iterator it = cman.iterator();
      while (it.hasNext()) {
        ConfigurationManager element = (ConfigurationManager) it.next();
        if (element.supportComponentListing()) {
          return element.getResourceList();
        }
      }
      return null;
    }
    
    public void setCommandLineArguments(List args) {
      Iterator it = cman.iterator();
      while (it.hasNext()) {
        ConfigurationManager element = (ConfigurationManager) it.next();
        element.setCommandLineArguments(args);
      }
    }
    
    public boolean supportComponentListing() {
      Iterator it = cman.iterator();
      while (it.hasNext()) {
        ConfigurationManager element = (ConfigurationManager) it.next();
        if (element.supportComponentListing()) {
          return true;
        }
      }
      return false;
    }
    public boolean supportParameterFetching() {
      Iterator it = cman.iterator();
      while (it.hasNext()) {
        ConfigurationManager element = (ConfigurationManager) it.next();
        if (element.supportParameterFetching()) {
          return true;
        }
      }
      return false;
    }
}
} // StandartDispatcher
