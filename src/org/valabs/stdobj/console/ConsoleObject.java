package com.novel.stdobj.console;

import java.util.ArrayList;

import com.novel.nms.messages.ModuleAboutMessage;
import com.novel.nms.messages.ModuleAboutReplyMessage;
import com.novel.nms.messages.ModuleStatusMessage;
import com.novel.nms.messages.ModuleStatusReplyMessage;
import com.novel.odisp.common.Message;
import com.novel.odisp.common.StandartODObject;
import com.novel.stdmsg.ODCleanupMessage;
import com.novel.stdmsg.ODObjectLoadedMessage;

/** Объект ODISP реализующий консольный интерфейс доступа к менеджеру.
 * 
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">Валентин А. Алексеев</a>
 * @author (C) 2003-2004 <a href="mailto:dron@novel-il.ru">Андрей А. Порохин</a>
 * @version $Id: ConsoleObject.java,v 1.21 2004/08/18 12:48:40 valeks Exp $
 */
public class ConsoleObject extends StandartODObject {
  /** Имя объекта */
  public final static String NAME = "console";
  /** Полное название объекта. */
  private static String FULLNAME = "ODISP Message Console"; 
  /** Версия объекта. */
  private static String VERSION = "0.3.0";
  /** Дополнительная информация. */
  private static String COPYRIGHT = "(C) 2003-2004 Valentin A. Alekseev, Andrew A. Porohin";
  
  /** Поток читающий ввод с консоли. */
  private ConsoleReader reader;

  /** Обработчик входящих сообщений.
   * 
   * @param msg сообщение
   */
  public final void handleMessage(final Message msg) {
    logger.finest("console object -- processing " + msg);
    if (ODObjectLoadedMessage.equals(msg)) {
      reader = new ConsoleReader(getObjectName(), dispatcher, logger);
      reader.start();
    } else if (ODCleanupMessage.equals(msg)) {
      cleanUp(0);
    } else if (ModuleAboutMessage.equals(msg)) {
    	Message m = dispatcher.getNewMessage();
    	ModuleAboutReplyMessage.setup(m, msg.getOrigin(), getObjectName(), msg.getId());
    	ModuleAboutReplyMessage.setName(m, FULLNAME);
    	ModuleAboutReplyMessage.setVersion(m, VERSION);
    	ModuleAboutReplyMessage.setCopyright(m, COPYRIGHT);
    	dispatcher.send(m);
    } else if (ModuleStatusMessage.equals(msg)) {
    	Message m = dispatcher.getNewMessage();
    	ModuleStatusReplyMessage.setup(m, msg.getOrigin(), getObjectName(), msg.getId());
    	ModuleStatusReplyMessage.setRunningState(m, "No error.");
    	ModuleStatusReplyMessage.setRunningTasks(m, new ArrayList());
    	ModuleStatusReplyMessage.setCompletedTasks(m, new ArrayList());
    	ModuleStatusReplyMessage.setFailedTasks(m, new ArrayList());
    	dispatcher.send(m);
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
