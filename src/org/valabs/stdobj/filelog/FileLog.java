package com.novel.stdobj.filelog;

import com.novel.odisp.common.PollingODObject;
import com.novel.odisp.common.Message;
import com.novel.stdobj.simpleconfig.SimpleConfig;
import com.novel.stdmsg.ODCleanupMessage;
import com.novel.stdmsg.ODObjectLoadedMessage;
import com.novel.stdmsg.ODResourceAcquiredMessage;
import com.novel.stdmsg.ODAcquireMessage;
import com.novel.stdmsg.ODReleaseMessage;
import com.novel.stdmsg.ODRemoveDepMessage;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

/** ������ ����������� ���������� ������������ ������� �������� ����� ��������.
* @author �������� �. ��������
* @author (�) 2003, ��� "�����-��"
* @version $Id: FileLog.java,v 1.16 2004/02/13 15:16:03 valeks Exp $
*/
public class FileLog extends PollingODObject {
  /** ����� ������. */
  private PrintWriter out;
  /** ������ �������� ��������. */
  private List patterns;
  /** ��������� ���������� ���������.
   * @param msg ���������
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
	  scfg.load(new FileInputStream(scfg.DEFAULT_CONFIG));
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
	  logger.warning("unable to find config file: " + scfg.DEFAULT_CONFIG);
	} catch (IOException e) {
	  logger.warning("unable to read config file " + scfg.DEFAULT_CONFIG);
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

  /** �����.
   * @param type ��� ������
   * @return ��� ������
   */
  public final int cleanUp(final int type) {
    if (out != null) {
      out.close();
    }
    doExit = true;
    return 0;
  }
  /** �������� ������ �������-�������.
   * @param id ����� �������
   */
  public FileLog(final Integer id) {
    super("log" + id);
  }
  /** �������� ������ �������� �������.
   * @return ������ ��������
   */
  public final String[] getProviding() {
    String[] res = {
      "log"
    };
    return res;
  }

  /** �������� ������ ������������ �������.
   * @return ������ ������������
   */
  public final String[] getDepends() {
    String[] res = {
      "stddispatcher",
      "com.novel.stdobj.simpleconfig.SimpleConfig"
    };
    return res;
  }
}
