package com.novel.stdobj.console;

import java.util.logging.Logger;
import com.novel.odisp.common.Dispatcher;
import com.novel.odisp.common.Message;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;


/** ����� �������� ������ � �������.
 * @author �������� �. ��������
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ConsoleReader.java,v 1.5 2004/01/16 14:31:57 valeks Exp $
 */

public class ConsoleReader extends Thread {
  /** ������� ��������� ������. */
  private boolean doExit;
  /** ��� ODISP �������. */
  private String objectName;
  /** ������. */
  private Logger logger;
  /** ������ �� ����������. */
  private Dispatcher dispatcher;
  /** ����� �����. */
  private BufferedReader inp
    = new BufferedReader(new InputStreamReader(System.in));
  /** ����������� ������� ���������� ���� � �������.
   * @param oName ��� ODISP-�������
   * @param disp ��������� ODISP
   * @param log ������ �� ������
   */
  public ConsoleReader(final String oName,
		       final Dispatcher disp,
		       final Logger log) {
    super("ConsoleReader");
    dispatcher = disp;
    logger = log;
    objectName = oName;
  }

  /**
   */
  public final void run() {
    try {
      System.out.print("action> ");
      String action, tmp;
      while ((action = inp.readLine()) != null) {
	System.out.print("destination> ");
	Message m
	  = dispatcher.getNewMessage(action, inp.readLine(), objectName, 0);
	System.out.print("params? ");
	while (!inp.readLine().equals("")) {
	  System.out.print("int|str? ");
	  tmp = inp.readLine();
	  System.out.print("value> ");
	  if (tmp.startsWith("i")) {
	    m.addField(new Integer(inp.readLine()));
	  } else {
	    m.addField(new String(inp.readLine()));
	  }
	  System.out.print("more? ");
	}
	dispatcher.sendMessage(m);
	System.out.print("action> ");
      }
      if (doExit) {
	return;
      }
    } catch (IOException e) {
      logger.finest("ConsoleReader: Terminal connection lost. Quitting.");
    }
    return;
  }

  /** ���������� ������. */
  public final synchronized void exit() {
    //    logger.finest("ConsoleReader: normal shutdown.");
    try {
      inp.close();
    } catch (IOException e) { /*NOP*/ }
    doExit = true;
  }
} // ConsoleReader
