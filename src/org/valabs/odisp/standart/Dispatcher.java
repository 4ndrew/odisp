package com.novel.odisp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.logging.Logger;

import com.novel.stdmsg.StandartMessage;
import com.novel.odisp.common.Message;
import com.novel.odisp.common.ResourceManager;
import com.novel.odisp.common.ObjectManager;
import com.novel.odisp.common.Dispatcher;
//import com.novel.stdmsg.*;

/** ����������� ��������� ODISP.
 * ����������� ��������� ��������� ��������� ��������� ����� ��������� ����
 * � ���������� ���������� ���������.
 * @author �������� �. ��������
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: Dispatcher.java,v 1.31 2004/02/13 22:22:50 valeks Exp $
 */
public class StandartDispatcher implements Dispatcher {
  /** ������. */
  private static Logger log = Logger.getLogger("com.novel.odisp");
  /** �������� ��������. */
  private ResourceManager rman = new StandartResourceManager(this);
  /** �������� ��������. */
  private ObjectManager oman = new StandartObjectManager(this);
  /** ���-�� �������� �������. */
  private int objCount = 0;

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
   * ��������� multicast �������� ���������
   * @param message ��������� ��� �������
   * @deprecated ���������� ������������ send(Message)
   */
  public final void sendMessage(final Message message) {
    send(message);
  }

  /** ��������� ��� �������� ���� ��� ������� ���������.
   * @param messageList ������ ��������� ��� �������
   * @deprecated ���������� ������������ send(Message[])
   */
  public final void sendMessages(final Message[] messageList) {
    send(messageList);
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
  /** ����������� ����������� �������������� ����� ��������.
   * �� ������ ������
   * @param objs ������ �������� ��� ��������
   */
  public StandartDispatcher(final List objs) {
    log.info(toString() + " starting up...");
    StandartDispatcherHandler stdh
      = new StandartDispatcherHandler(new Integer(0));
    ObjectEntry oe
      = new ObjectEntry(stdh.getClass().getName(), 0, stdh.getDepends(), stdh.getProviding());
    stdh.setDispatcher(this);
    oe.setObject(stdh);
    oman.getObjects().put("stddispatcher", oe);
    oman.loadPending();
    Message runthr = getNewMessage("od_set_run_thread", "stddispatcher", "G0D", 0);
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
    runthr.addField(t);
    oman.send(runthr);
    Iterator it = objs.iterator();
    Pattern p = Pattern.compile("(o:|(r:)(\\d+:)?)([^:]+)(:(.*))?");
    //                            type    mult     class  param
    while (it.hasNext()) {
      int mult = 1;
      String param = "";
      String className = (String) it.next();
      Matcher m = p.matcher(className);
      m.find();
      String parsedLine = "";
      for (int i = 0; i != m.groupCount(); i++) {
	parsedLine += i + "='" + m.group(i) + "' ";
      }
      log.finest(parsedLine);
      if (m.groupCount() == 6) {
	if (m.group(1).equals("o:")) {
	  oman.loadObject(m.group(4));
	}
	if (m.group(1).startsWith("r:")) {
	  if (m.group(3) != null) {
	    int len = m.group(3).length() - 1;
	    mult = new Integer(m.group(3).substring(0, len)).intValue();
	  }
	  if (m.group(5) != null) {
	    param = m.group(5).substring(1);
	  }
	  rman.loadResource(m.group(4), mult, param);
	}
	oman.loadPending();
      }
    }
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
	BufferedReader cfg = new BufferedReader(new FileReader(args[0]));
	List objs = new ArrayList();
	String s;
	while ((s = cfg.readLine()) != null) {
	  if (!s.startsWith("#")) {
	    objs.add(s);
	  }
	}
	new StandartDispatcher(objs);
      } catch (FileNotFoundException e) {
	log.severe("[e] configuration file " + args[0] + " not found.");
      } catch (IOException e) {
	log.severe("[e] unable to read configuration file.");
      }
    }
  }
} // StandartDispatcher
