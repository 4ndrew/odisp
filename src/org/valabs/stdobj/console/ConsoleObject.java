package org.valabs.stdobj.console;

import java.util.Enumeration;

import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.StandartODObject;
import org.valabs.stdmsg.ODCleanupMessage;
import org.valabs.stdmsg.ODObjectLoadedMessage;
import org.valabs.stdmsg.TranslatorGetTranslationMessage;
import org.valabs.stdmsg.TranslatorGetTranslationReplyMessage;
import org.valabs.stdobj.translator.Translator;

/**
 * Объект ODISP реализующий консольный интерфейс доступа к менеджеру.
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">Валентин А.
 *         Алексеев </a>
 * @author (C) 2003-2004 <a href="mailto:dron@novel-il.ru">Андрей А. Порохин
 *         </a>
 * @version $Id: ConsoleObject.java,v 1.25 2005/01/25 19:03:34 valeks Exp $
 */
public class ConsoleObject extends StandartODObject {
  /** Имя объекта */
  public final static String NAME = "console";

  /** Полное название объекта. */
  private static String FULLNAME = "ODISP Message Console";

  /** Версия объекта. */
  private static String VERSION = "0.3.1";

  /** Дополнительная информация. */
  private static String COPYRIGHT = "(C) 2003-2004 Valentin A. Alekseev, Andrew A. Porohin";

  /** Выполнять ли трансляцию констант в сообщениях. */
  private static boolean doTranslation = false;

  /** Поток читающий ввод с консоли. */
  private ConsoleReader reader;

  /** Транслятор. */
  private Translator tr;

  /**
   * Обработчик входящих сообщений.
   * @param msg сообщение
   */
  public final void handleMessage(final Message msg) {
    logger.finest("console object -- processing " + msg);
    if (ODObjectLoadedMessage.equals(msg)) {
      reader = new ConsoleReader(getObjectName(), dispatcher, logger);
      reader.start();
      if (getParameter("hasTranslator", "no").equals("yes")) {
        tr = (Translator) dispatcher.getResourceManager().resourceAcquire(
            Translator.class.getName());
        Message m = dispatcher.getNewMessage();
        TranslatorGetTranslationMessage.setup(m, "translator-server",
            getObjectName(), msg.getId());
        TranslatorGetTranslationMessage.setLanguage(m, getParameter("language",
            "ru"));
        dispatcher.send(m);
      }
    } else if (ODCleanupMessage.equals(msg)) {
      cleanUp(0);
    } else if (TranslatorGetTranslationReplyMessage.equals(msg)) {
      tr.putAll(TranslatorGetTranslationReplyMessage.getTranslation(msg));
      doTranslation = true;
      logger.fine("Console translation enabled");
    } else {
      System.out.println("Received:");
      String msgToString = msg.toString(true);
      if (doTranslation) {
        Enumeration e = tr.keys();
        while (e.hasMoreElements()) {
          String element = (String) e.nextElement();
          String replacement = tr.translate(element, element);
          msgToString = msgToString.replaceAll(element, replacement + "["
              + element + "]");
        }
      }
      System.out.println(msgToString);
    }
    return;
  }

  /**
   * Выход.
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

  /**
   * Конструктор объекта с заданным порядковым номером.
   * @param id номер
   */
  public ConsoleObject(final Integer id) {
    super(NAME + id, FULLNAME, VERSION, COPYRIGHT);
  }

  /**
   * Вернуть список сервисов.
   * @return список сервисов
   */
  public final String[] getProviding() {
    String[] res = { NAME };
    return res;
  }

  /**
   * Вернуть список зависимостей.
   * @return список зависимостей
   */
  public final String[] getDepends() {
    if (getParameter("hasTranslator", "no").equals("no")) {
      String[] res = { "dispatcher" };
      return res;
    } else {
      String[] res = { "dispatcher", "translator-server",
          Translator.class.getName(), };
      return res;
    }
  }
}