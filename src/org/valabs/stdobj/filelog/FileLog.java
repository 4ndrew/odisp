package com.novel.stdobj.filelog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.novel.odisp.common.Message;
import com.novel.odisp.common.PollingODObject;
import com.novel.stdmsg.ODAcquireMessage;
import com.novel.stdmsg.ODCleanupMessage;
import com.novel.stdmsg.ODObjectLoadedMessage;
import com.novel.stdmsg.ODReleaseMessage;
import com.novel.stdmsg.ODRemoveDepMessage;
import com.novel.stdmsg.ODResourceAcquiredMessage;
import com.novel.stdobj.simpleconfig.SimpleConfig;

/** Объект реализующий простейшую журнализацию событий согласно файлу шаблонов.
* @author Валентин А. Алексеев
* @author (С) 2003, НПП "Новел-ИЛ"
* @version $Id: FileLog.java,v 1.20 2004/05/11 09:56:32 valeks Exp $
*/
public class FileLog extends PollingODObject {
  /** Поток вывода. */
  private PrintWriter out;
  /** Список шаблонов действий. */
  private List patterns;
  /** Обработка пришедшего сообщения.
   * @param msg сообщение
   */
  public final void handleMessage(final Message msg) {
    if (msg instanceof ODCleanupMessage
	&& msg.getDestination().equals(getObjectName())) {
      cleanUp(((ODCleanupMessage) msg).getReason());
      return;
    }
    if (msg instanceof ODObjectLoadedMessage
	&& msg.getDestination().equals(getObjectName())) {
      setMatch(".*");
      ODAcquireMessage m = new ODAcquireMessage(getObjectName(), 0);
      m.setResourceName("com.novel.stdobj.simpleconfig.SimpleConfig");
      m.setWillBlock(true);
      dispatcher.send(m);
      return;
    }
    if (msg instanceof ODResourceAcquiredMessage
	&& msg.getDestination().equals(getObjectName())) {
      // we acquired SimpleConfig resource
      String className
	= (String) ((ODResourceAcquiredMessage) msg).getResourceName();
      if (className.startsWith("com.novel.stdobj.simpleconfig.SimpleConfig")) {
	SimpleConfig scfg
	  = (SimpleConfig) ((ODResourceAcquiredMessage) msg).getResource();
	try {
	  scfg.load(new FileInputStream(SimpleConfig.DEFAULT_CONFIG));
	  try {
	    File hLogFile = new File(scfg.getProperty("log_logfile", "odisp.log"));
	    if (!hLogFile.exists()) {
	      hLogFile.createNewFile();
	    }
	    out = new PrintWriter(new FileWriter(hLogFile, true));
	    BufferedReader pfile
	      = new BufferedReader(new FileReader(scfg.getProperty("log_patternfile", "odisp-log.ptn")));
	    String s;
	    patterns = new ArrayList();
	    while ((s = pfile.readLine()) != null) {
	      patterns.add(s);
	    }
	    pfile.close();
	  } catch (FileNotFoundException e) {
	    logger.warning("unable to open logfile.");
	  } catch (IOException e) {
	    logger.warning("unable to read either log file or pattern file");
	  }
	} catch (FileNotFoundException e) {
	  logger.warning("unable to find config file: " + SimpleConfig.DEFAULT_CONFIG);
	} catch (IOException e) {
	  logger.warning("unable to read config file " + SimpleConfig.DEFAULT_CONFIG);
	}
	Message[] m = {
	  new ODReleaseMessage(getObjectName(), msg.getId()),
	  new ODRemoveDepMessage(getObjectName(), 0),
	};
	((ODReleaseMessage) m[0]).setResourceName(className).setResource(scfg);
	((ODRemoveDepMessage) m[1]).setDepName("com.novel.stdobj.simpleconfig.SimpleConfig");
	dispatcher.send(m);
      }
      return;
    }
    if (patterns == null || out == null) {
      return;
    }
    boolean doMatch = false;
    Iterator it = patterns.iterator();
    while (it.hasNext()) {
      if (Pattern.matches((String) it.next(), msg.getAction())) {
	doMatch = true;
      }
    }
    if (doMatch) {
      out.write(new Date().toString() + " -- " + msg + "\n");
      out.flush();
    }
  }

  /** Выход.
   * @param type код выхода
   * @return код выхода
   */
  public final int cleanUp(final int type) {
    if (out != null) {
      out.close();
    }
    return 0;
  }
  /** Создание нового объекта-журнала.
   * @param id номер объекта
   */
  public FileLog(final Integer id) {
    super("log" + id);
  }
  /** Получить список сервисов объекта.
   * @return список сервисов
   */
  public final String[] getProviding() {
    String[] res = {
      "log"
    };
    return res;
  }

  /** Получить список зависимостей объекта.
   * @return список зависимостей
   */
  public final String[] getDepends() {
    String[] res = {
      "dispatcher",
      "com.novel.stdobj.simpleconfig.SimpleConfig"
    };
    return res;
  }
}
