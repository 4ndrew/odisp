package org.valabs.odisp.standart;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.valeks.xlang.parser.Parser;
import org.valeks.xlang.parser.Tag;
import org.valeks.xlang.parser.XLangException;

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
 * @version $Id: Dispatcher.java,v 1.53 2004/10/28 22:10:27 valeks Exp $
 */
public class Dispatcher implements org.valabs.odisp.common.Dispatcher, ExceptionHandler {
  /** ������. */
  private static Logger log = Logger.getLogger(Dispatcher.class.getName());
  /** �������� ��������. */
  private ResourceManager rman = new org.valabs.odisp.standart.ResourceManager(this);
  /** �������� ��������. */
  private ObjectManager oman = new org.valabs.odisp.standart.ObjectManager(this);
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
				     final int inReplyTo) {
    return new StandartMessage(action, destination, origin, inReplyTo);
  }

  /** ������� ����� ������ ���������.
   * @return ������ �� ���������
   */
  public final Message getNewMessage() {
    return new StandartMessage();
  }

  /** �������� ���������� � ���������� ��� ��������� ����.
   * @param childTag ���
   */
  private Map getParamsForTag(final Tag childTag) {
    Map params = null;
    if (childTag.getChild().size() != 0) {
      params = new HashMap();
      // ������� ������� -- ���������� �������������� �� ������ � ��������� ������
      Iterator cit = childTag.getChild().iterator();
      while (cit.hasNext()) {
        Tag ctag = (Tag) cit.next();
        if (ctag.getName().equalsIgnoreCase("param")) {
          String paramName = (String) ctag.getAttributes().get("name");
          String paramValue = (String) ctag.getAttributes().get("value");
          if (paramName != null && paramValue != null) {
            params.put(paramName, paramValue);
          }
        }
      }
    }
    return params;
  }

  /** ����������� ����������� �������������� ����� ��������.
   * �� ������ ������
   * @param docTag 
   */
  public Dispatcher(final Tag docTag) {
    log.info(toString() + " starting up...");
    Iterator it = docTag.getChild().iterator();
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
    Map resources = new HashMap();
    Map objects = new HashMap();
    while (it.hasNext()) {
      Tag curt = (Tag) it.next();
      if (curt.getName().equalsIgnoreCase("object")) {
        String className = (String) curt.getAttributes().get("name");
        if (className == null) {
          log.warning("object tag has no name attribute. ignoring.");
          continue;
        }
        Map params = getParamsForTag(curt);
        objects.put(className, params);
      } else if (curt.getName().equalsIgnoreCase("resource")) {
        String className = (String) curt.getAttributes().get("name");
        if (className == null) {
          log.warning("resource tag has no name attribute. ignoring.");
          continue;
        }
        Map params = getParamsForTag(curt);
        resources.put(className, params);
      }
    }

    it = resources.keySet().iterator();
    while (it.hasNext()) {
      String className = (String) it.next();
      int mult = -1;
      if (resources.get(className) != null && ((Map) resources.get(className)).get("mult") != null) {
        mult = new Integer((String) ((Map) resources.get(className)).get("mult")).intValue();
        
      }
      rman.loadResource(className, mult, (Map) resources.get(className));
    }
    it = objects.keySet().iterator();
    while (it.hasNext()) {
      String className = (String) it.next();
      oman.loadObject(className, (Map) objects.get(className));
    }
    oman.loadPending();
    t.start();
    try {
      t.join();
    } catch (InterruptedException e) {

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
  public static void main(final String[] args) {
    log.setLevel(java.util.logging.Level.ALL);
    if (args.length != 1) {
		usage();
    } else {
      try {
	    InputStream inp = new FileInputStream(args[0]);
        Parser p = new Parser(inp);
	    new Dispatcher(p.getRootTag());
      } catch (FileNotFoundException e) {
	    log.severe("configuration file " + args[0] + " not found.");
      } catch (XLangException e) {
        log.severe("configuration file " + args[0] + " contains unrecoverable errors: " + e);
      }
    }
  }
  
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

} // StandartDispatcher
