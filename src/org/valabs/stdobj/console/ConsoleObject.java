package com.novel.stdobj.console;

import com.novel.odisp.common.PollingODObject;
import com.novel.odisp.common.Message;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
/** Объект ODISP реализующий консольный интерфейс доступа к менеджеру
* @author Валентин А. Алексеев
* @author (C) 2003, НПП "Новел-ИЛ"
* @version $Id: ConsoleObject.java,v 1.11 2003/12/02 14:49:07 valeks Exp $
*/
public class ConsoleObject extends PollingODObject {
  /** Поток читающий ввод с консоли */
  private ConsoleReader reader;
  /** Обработчик входящих сообщений 
   * @param msg сообщение
   */
  public void handleMessage(Message msg) {
    logger.finest("console object -- processing " + msg);
    if (msg.getAction().equals("od_object_loaded")) {
      reader = new ConsoleReader(getObjectName(), dispatcher, logger);
      reader.start();
    } else if (msg.getAction().equals("od_cleanup")) {
      cleanUp(0);
    } else {
      System.out.println("Received:");
      System.out.println(msg.toString());
      if (msg.getFieldsCount() > 0) {

	System.out.println("Fields dump:");
      }
      for (int i = 0; i < msg.getFieldsCount(); i++) {
	System.out.print(i + ":");
	System.out.println(msg.getField(i));
      }
    }
    return;
  }
  /** Выход 
   * @param type признак выхода
   * @return код возврата
   */
  public int cleanUp(int type) {
    logger.finest("Cleaning up...");
    if (reader != null) {
      reader.exit();
    }
    doExit = true;
    return 0;
  }
  /** Конструктор объекта с заданным порядковым номером
   * @param id номер
   */
  public ConsoleObject(Integer id) {
    super("console" + id);
  }
  /** Вернуть список сервисов
   * @return список сервисов
   */
  public String[] getProviding() {
    String res[] = {"console"};
    return res;
  }
  /** Вернуть список зависимостей 
   * @return список зависимостей
   */
  public String[] getDepends() {
    String res[] = {
      "stddispatcher",
      "log"
    };
    return res;
  }
}
