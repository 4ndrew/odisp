package org.valabs.stdobj.filelog;

import java.io.BufferedReader;
import java.io.File;
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

import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.StandartODObject;
import org.valabs.stdmsg.ODCleanupMessage;
import org.valabs.stdmsg.ODObjectLoadedMessage;

/** ������ ����������� ���������� ������������ ������� �������� ����� ��������.
* @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
* @version $Id: FileLog.java,v 1.33 2004/08/26 11:32:01 valeks Exp $
*/
public class FileLog extends StandartODObject {
	/** ��� �������. */
	public static String NAME = "log";
  /** ����� ������. */
  private PrintWriter out;
  /** ������ �������� ��������. */
  private List patterns;
  /** ��������� ���������� ���������.
   * @param msg ���������
   */
  public final void handleMessage(final Message msg) {
    if (ODCleanupMessage.equals(msg)
	&& msg.getDestination().equals(getObjectName())) {
      cleanUp(ODCleanupMessage.getReason(msg).intValue());
      return;
    }
    if (ODObjectLoadedMessage.equals(msg)
	&& msg.getDestination().equals(getObjectName())) {
	  try {
	    File hLogFile = new File(getParameter("logfile", "odisp.log"));
	    if (!hLogFile.exists()) {
	      hLogFile.createNewFile();
	    }
	    out = new PrintWriter(new FileWriter(hLogFile, true));
	    BufferedReader pfile
	      = new BufferedReader(new FileReader(getParameter("patternfile", "odisp-log.ptn")));
	    String s;
	    patterns = new ArrayList();
	    while ((s = pfile.readLine()) != null) {
	      patterns.add(s);
	    }
	    pfile.close();
	  } catch (FileNotFoundException e) {
	  	dispatcher.getExceptionHandler().signalException(e);
	    logger.warning("unable to open logfile.");
	  } catch (IOException e) {
	  	dispatcher.getExceptionHandler().signalException(e);
	    logger.warning("unable to read either log file or pattern file");
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
    return 0;
  }
  /** �������� ������ �������-�������.
   * @param id ����� �������
   */
  public FileLog(final Integer id) {
    super(NAME + id);
    setMatch(".*");
  }
  /** �������� ������ �������� �������.
   * @return ������ ��������
   */
  public final String[] getProviding() {
    String[] res = {
      NAME
    };
    return res;
  }

  /** �������� ������ ������������ �������.
   * @return ������ ������������
   */
  public final String[] getDepends() {
    String[] res = {
      "dispatcher",
    };
    return res;
  }
}
