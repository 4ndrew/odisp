package com.novel.stdobj.console;

import java.util.logging.Logger;
import com.novel.odisp.common.Dispatcher;
import com.novel.odisp.common.Message;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;


/** Класс читающий данные с консоли.
 * 
 * @author <a href="valeks@novel-il.ru">Валентин А. Алексеев</a>
 * @author <a href="dron@novel-il.ru">Андрей А. Порохин</a>
 * @author (C) 2003-2004 НПП "Новел-ИЛ"
 * @version $Id: ConsoleReader.java,v 1.7 2004/03/17 11:58:06 dron Exp $
 */

public class ConsoleReader extends Thread {
  /** Признак окончания работы. */
  private boolean doExit;
  /** Имя ODISP объекта. */
  private String objectName;
  /** Журнал. */
  private Logger logger;
  /** Ссылка на диспетчера. */
  private Dispatcher dispatcher;
  /** Поток ввода. */
  private BufferedReader inp
    = new BufferedReader(new InputStreamReader(System.in));

  /** Конструктор объекта слушающего ввод с консоли.
   * 
   * @param oName имя ODISP-объекта
   * @param disp диспетчер ODISP
   * @param log ссылка на журнал
   */
  public ConsoleReader(final String oName,
		       final Dispatcher disp,
		       final Logger log) {
    super("ConsoleReader");
    dispatcher = disp;
    logger = log;
    objectName = oName;
  }

  /** Точка входа потока обработки ввода-вывода.
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
        int paramCount = 0;
	while (!inp.readLine().equals("")) {
	  System.out.print("int|str? ");
	  tmp = inp.readLine();
	  System.out.print("value> ");
	  if (tmp.startsWith("i")) {
            try {
	      m.addField("" + paramCount, new Integer(inp.readLine()));
            } catch (NumberFormatException e) {
              System.out.println("(ConsoleReader) NumberFormatException: please, retry.");
            }
	  } else {
	    m.addField("" + paramCount, new String(inp.readLine()));
	  }
          paramCount++;
	  System.out.print("more? ");
	}
	dispatcher.send(m);
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

  /** Завершение работы.
   */
  public final synchronized void exit() {
    try {
      inp.close();
    } catch (IOException e) { /*NOP*/ }
    doExit = true;
  }
}
