package org.valabs.stdobj.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import org.doomdark.uuid.UUID;
import org.valabs.odisp.common.Dispatcher;
import org.valabs.odisp.common.Message;


/** Класс читающий данные с консоли.
 * 
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">Валентин А. Алексеев</a>
 * @author (C) 2003-2004 <a href="mailto:dron@novel-il.ru">Андрей А. Порохин</a>
 * @version $Id: ConsoleReader.java,v 1.17 2005/01/26 22:10:30 valeks Exp $
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
    setDaemon(true);
    dispatcher = disp;
    logger = log;
    objectName = oName;
  }

  /** Точка входа потока обработки ввода-вывода.
   */
  public final void run() {
    try {
      System.out.print("action> ");
      String action, tmp, fieldName;
      while ((action = inp.readLine()) != null) {
        System.out.print("destination> ");
        Message m
          = dispatcher.getNewMessage(action, inp.readLine(), objectName, UUID.getNullUUID());
        System.out.print("params? ");
        int paramCount = 0;
        while (!inp.readLine().equals("")) {
          System.out.print("int|str? ");
          tmp = inp.readLine();
          System.out.print("name> ");
          fieldName = inp.readLine();
          System.out.print("value> ");
          if (tmp.startsWith("i")) {
            try {
              m.addField((fieldName.length() > 0) ? fieldName : ("" + paramCount++),
                new Integer(inp.readLine()));
            } catch (NumberFormatException e) {
              System.out.println("(ConsoleReader) NumberFormatException: please, retry.");
            }
          } else {
            m.addField((fieldName.length() > 0) ? fieldName : ("" + paramCount++),
              new String(inp.readLine()));
          }
          System.out.print("more? ");
        }
        m.setCorrect(true);
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
      this.interrupt();
    } catch (IOException e) { /*NOP*/ }
    doExit = true;
  }
}
