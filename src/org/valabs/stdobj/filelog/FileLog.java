package com.novel.stdobj.filelog;

import com.novel.odisp.common.PollingODObject;
import com.novel.odisp.common.Message;
import com.novel.stdobj.simpleconfig.SimpleConfig;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

/** Объект реализующий простейшую журнализацию событий согласно файлу шаблонов
* @author Валентин А. Алексеев
* @author (С) 2003, НПП "Новел-ИЛ"
* @version $Id: FileLog.java,v 1.9 2003/11/22 14:04:10 valeks Exp $
*/
public class FileLog extends PollingODObject {
  /** Поток вывода */
  private PrintWriter out;
  /** Список шаблонов действий */
  private List patterns;
  public void handleMessage(Message msg) {
    if (msg.getAction().equals("od_cleanup") && msg.getDestination().equals(getObjectName())) {
      cleanUp(((Integer) msg.getField(0)).intValue());
      return;
    }
    if (msg.getAction().equals("od_object_loaded") && msg.getDestination().equals(getObjectName())) {
      setMatch(".*");
      Message m = dispatcher.getNewMessage("od_acquire", "stddispatcher", getObjectName(), 0);
      m.addField(new String("com.novel.stdobj.simpleconfig.SimpleConfig"));
      m.addField(new Boolean(true));
      dispatcher.sendMessage(m);
      return;
    }
    if (msg.getAction().equals("resource_acquired") && msg.getDestination().equals(getObjectName())) { // we acquired SimpleConfig resource
      if (msg.getFieldsCount() == 2) {
	String className = (String) msg.getField(0);
	if (className.startsWith("com.novel.stdobj.simpleconfig.SimpleConfig")) {
	  SimpleConfig scfg = (SimpleConfig) msg.getField(1);
	  scfg.readConfig("simpleconfig.dat");
	  try {
	    File hLogFile = new File(scfg.getValue("log_logfile"));
	    if (!hLogFile.exists()) {
	      hLogFile.createNewFile();
	    }
	    out = new PrintWriter(new FileWriter(hLogFile, true));
	    BufferedReader pfile = new BufferedReader(new FileReader(scfg.getValue("log_patternfile")));
	    String s;
	    patterns = new ArrayList();
	    while ((s = pfile.readLine()) != null) {
	      patterns.add(s);
	      logger.finer("adding pattern " + s);
	    }
	    pfile.close();
	  } catch (FileNotFoundException e) { logger.warning("unable to open logfile.");
	  } catch (IOException e) { logger.warning("unable to read either log file or pattern file");}
	  Message[] m = {
	    dispatcher.getNewMessage("od_release", "stddispatcher", getObjectName(), msg.getId()),
	    dispatcher.getNewMessage("od_remove_dep", "stddispatcher", getObjectName(), 0)
	  };
	  m[0].addField(msg.getField(0));
	  m[0].addField(scfg);
	  m[1].addField("com.novel.stdobj.simpleconfig.SimpleConfig");
	  dispatcher.sendMessages(m);
	}
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
  public int cleanUp(int type) {
    if (out != null) {
      out.close();
    }
    doExit = true;
    return 0;
  }
  public FileLog(Integer id) {
    super("log" + id);
  }
  public String[] getProviding() {
    String res[] = {
      "log"
    };
    return res;
  }
  public String[] getDepends() {
    String res[] = {
      "stddispatcher",
      "com.novel.stdobj.simpleconfig.SimpleConfig"
    };
    return res;
  }
}
