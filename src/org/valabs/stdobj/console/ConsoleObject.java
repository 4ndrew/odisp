package com.novel.stdobj.console;

import com.novel.odisp.common.Message;
import com.novel.odisp.common.StandartODObject;

/** Объект ODISP реализующий консольный интерфейс доступа к менеджеру.
 * 
 * @author <a href="mailto:valeks@novel-il.ru">Валентин А. Алексеев</a>
 * @author <a href="mailto:dron@novel-il.ru">Андрей А. Порохин</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ConsoleObject.java,v 1.19 2004/06/25 13:15:31 dron Exp $
 */
public class ConsoleObject extends StandartODObject {
  /** Имя объекта */
  public final static String NAME = "console"; 
  /** Поток читающий ввод с консоли. */
  private ConsoleReader reader;

  /** Обработчик входящих сообщений.
   * 
   * @param msg сообщение
   */
  public final void handleMessage(final Message msg) {
    logger.finest("console object -- processing " + msg);
    if (msg.getAction().equals("od_object_loaded")) {
      reader = new ConsoleReader(getObjectName(), dispatcher, logger);
      reader.start();
    } else if (msg.getAction().equals("od_cleanup")) {
      cleanUp(0);
    } else {
      System.out.println("Received:");
      System.out.println(msg.toString(true));
    }
    return;
  }
  
  /** Выход.
   * 
   * @param type признак выхода
   * @return код возврата
   */
  public final int cleanUp(final int type) {
    logger.finest("Cleaning up...");
    if (reader != null) {
      reader.exit();
    }
    return 0;
  }
  
  /** Конструктор объекта с заданным порядковым номером.
   * 
   * @param id номер
   */
  public ConsoleObject(final Integer id) {
    super(NAME + id);
  }
  
  /** Вернуть список сервисов.
   * 
   * @return список сервисов
   */
  public final String[] getProviding() {
    String[] res = { NAME };
    return res;
  }
  
  /** Вернуть список зависимостей.
   * 
   * @return список зависимостей
   */
  public final String[] getDepends() {
    String[] res = { "dispatcher" };
    return res;
  }
}
