package com.novel.odisp;

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

import com.novel.odisp.common.Dispatcher;
import com.novel.odisp.common.Message;
import com.novel.odisp.common.ObjectManager;
import com.novel.odisp.common.ResourceManager;
import com.novel.stdmsg.StandartMessage;

/** ����������� ��������� ODISP.
 * ����������� ��������� ��������� ��������� ��������� ����� ��������� ����
 * � ���������� ���������� ���������.
 * @author �������� �. ��������
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: Dispatcher.java,v 1.42 2004/05/28 00:16:12 valeks Exp $
 */
public class StandartDispatcher implements Dispatcher {
  /** ������. */
  private static Logger log = Logger.getLogger("com.novel.odisp.StandartDispatcher");
  /** �������� ��������. */
  private ResourceManager rman = new StandartResourceManager(this);
  /** �������� ��������. */
  private ObjectManager oman = new StandartObjectManager(this);

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
    Iterator it = messageList.iterator();
    while (it.hasNext()) {
      send((Message) it.next());
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
  public StandartDispatcher(final Tag docTag) {
    log.info(toString() + " starting up...");
    Iterator it = docTag.getChild().iterator();
    oman.loadObject(StandartDispatcherHandler.class.getName(), null);
    while (it.hasNext()) {
      Tag curt = (Tag) it.next();
      if (curt.getName().equalsIgnoreCase("object")) {
        String className = (String) curt.getAttributes().get("name");
        if (className == null) {
          log.warning("object tag has no name attribute. ignoring.");
          continue;
        }
        Map params = getParamsForTag(curt);
        oman.loadObject(className, params);
      } else if (curt.getName().equalsIgnoreCase("resource")) {
        int mult = 1;
        String className = (String) curt.getAttributes().get("name");
        if (className == null) {
          log.warning("resource tag has no name attribute. ignoring.");
          continue;
        }
        String smult = (String) curt.getAttributes().get("mult");
        if (smult != null) {
          try {
            mult = new Integer(smult).intValue();
          } catch (NumberFormatException e) {
            log.warning("resource tag attribute mult has non-integer value. ignoring.");
          }
        }
        Map params = getParamsForTag(curt);
        rman.loadResource(className, mult, params);
      }
    }
    oman.loadPending();
    Message runthr = getNewMessage("od_set_run_thread", "dispatcher", "G0D", 0);
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
    t.start();
    runthr.addField("0", t);
    oman.send(runthr);
    try {
      t.join();
    } catch (InterruptedException e) {

    }
  }
  /** ������� ��������� �� ������ � ������ ������������ ����������. */
  public static void usage() {
    log.severe("Usage: java com.novel.odisp.StandartDispatcher <config>");
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
	    new StandartDispatcher(p.getRootTag());
      } catch (FileNotFoundException e) {
	    log.severe("configuration file " + args[0] + " not found.");
      } catch (XLangException e) {
        log.severe("configuration file " + args[0] + " contains unrecoverable errors: " + e);
      }
    }
  }
} // StandartDispatcher
