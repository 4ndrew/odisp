package com.novel.odisp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import com.novel.stdmsg.StandartMessage;
import com.novel.odisp.common.Message;
import com.novel.odisp.common.ResourceManager;
import com.novel.odisp.common.ObjectManager;
import com.novel.odisp.common.Dispatcher;
//import com.novel.stdmsg.*;

/** Стандартный диспетчер ODISP.
 * Стандартный диспетчер реализует пересылку сообщений между объектами ядра
 * и управление ресурсными объектами.
 * @author Валентин А. Алексеев
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: Dispatcher.java,v 1.33 2004/02/23 14:46:11 valeks Exp $
 */
public class StandartDispatcher implements Dispatcher {
  /** Журнал. */
  private static Logger log = Logger.getLogger("com.novel.odisp");
  /** Менеджер ресурсов. */
  private ResourceManager rman = new StandartResourceManager(this);
  /** Менеджер объектов. */
  private ObjectManager oman = new StandartObjectManager(this);
  /** Кол-во объектов системы. */
  private int objCount = 0;

  /** Доступ к менеджеру объектов. 
   * @return ссылка на менеджер объектов
   */
  public final ObjectManager getObjectManager() {
    return oman;
  }

  /** Доступ к менеджеру ресурсов. 
   * @return ссылка на менеджер ресурсов
   */
  public final ResourceManager getResourceManager() {
    return rman;
  }

  /** Интерфейс для объектов ядра для отсылки сообщений.
   * Реализует multicast рассылку сообщений
   * @param message сообщение для отсылки
   * @deprecated необходимо использовать send(Message)
   */
  public final void sendMessage(final Message message) {
    send(message);
  }

  /** Интерфейс для объектов ядра для отсылки сообщений.
   * @param messageList список сообщений для отсылки
   * @deprecated необходимо использовать send(Message[])
   */
  public final void sendMessages(final Message[] messageList) {
    send(messageList);
  }

  /** Интерфейс для объектов ядра для отсылки сообщений.
   * @param message сообщение для посылки
   */
  public final void send(final Message message) {
    oman.send(message);
  }

  /** Интерфейс для объектов ядра для отсылки сообщений.
   * @param messageList список сообщений для посылки
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

  /** Интерфейс для объектов ядра для отсылки сообщений.
   * @param messageList список сообщений для посылки
   */
  public final void send(final List messageList) {
    Iterator it = messageList.iterator();
    while (it.hasNext()) {
      send((Message) it.next());
    }
  }

  /** Интерфейс создания нового сообщения для сокрытия конкретной реализации
   * сообщений.
   * @param action действие которое несет сообщение
   * @param destination адресат сообщения
   * @param origin отправитель сообщения
   * @param inReplyTo идентификатор сообщения на которое производится ответ
   * @return Message созданное сообщение
   */
  public final Message getNewMessage(final String action,
				     final String destination,
				     final String origin,
				     final int inReplyTo) {
    return new StandartMessage(action, destination, origin, inReplyTo);
  }

  /** Создать новое пустое сообщение.
   * @return ссылка на сообщение
   */
  public final Message getNewMessage() {
    return new StandartMessage();
  }
  /** Конструктор загружающий первоначальный набор объектов.
   * на основе списка
   * @param objs список объектов для загрузки
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
    while (it.hasNext()) {
      int mult = 1;
      String param = "";
      String line = (String) it.next();
      boolean type = true; // true -- object, false -- resource
      StringTokenizer st = new StringTokenizer(line, ":");
      if (st.countTokens() < 1) {
	continue;
      }
      if (st.nextToken().equalsIgnoreCase("r")) {
	type = false;
      }
      String className = st.nextToken();
      if (st.hasMoreTokens()) {
	mult = new Integer(st.nextToken()).intValue();
      }
      if (type) { // объект
	oman.loadObject(className);
      } else { // ресурс
	rman.loadResource(className, mult);
      }
      oman.loadPending();
    }
    try {
      t.join();
    } catch (InterruptedException e) {

    }
  }
  /** Выводит сообщение об ошибке в случае некорректных параметров. */
  public static void usage() {
    log.severe("Usage: java com.novel.odisp.StandartDispatcher <config>");
    System.exit(0);
  }
  /** Точка входа в StandartDispatcher.
   * @param args по 0 должно содержать имя файла с перечислением классов,
   * которые необходимо загрузить
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
	  if (!s.startsWith("#") && s.length() != 0) {
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
