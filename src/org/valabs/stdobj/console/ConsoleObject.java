package org.valabs.stdobj.console;

import java.io.IOException;
import java.util.Enumeration;

import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.StandartODObject;
import org.valabs.stdmsg.ODObjectLoadedMessage;
import org.valabs.stdmsg.TranslatorGetTranslationMessage;
import org.valabs.stdmsg.TranslatorGetTranslationReplyMessage;
import org.valabs.stdobj.translator.Translator;

/**
 * ������ ODISP ����������� ���������� ��������� ������� � ���������.
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">�������� �.
 *         �������� </a>
 * @author (C) 2003-2004 <a href="mailto:dron@novel-il.ru">������ �. �������
 *         </a>
 * @version $Id: ConsoleObject.java,v 1.28 2005/07/14 11:01:16 valeks Exp $
 */
public class ConsoleObject extends StandartODObject {
  /** ��� ������� */
  public final static String NAME = "console";

  /** ������ �������� �������. */
  private static String FULLNAME = "ODISP Message Console";

  /** ������ �������. */
  private static String VERSION = "0.3.1";

  /** �������������� ����������. */
  private static String COPYRIGHT = "(C) 2003-2004 Valentin A. Alekseev, Andrew A. Porohin";

  /** ��������� �� ���������� �������� � ����������. */
  private static boolean doTranslation = false;

  /** ����� �������� ���� � �������. */
  private ConsoleReader reader;

  /** ����������. */
  private Translator tr;

  /**
   * ���������� �������� ���������.
   * @param msg ���������
   */
  public final void handleMessage(final Message msg) {
    logger.finest("console object -- processing " + msg);
    if (ODObjectLoadedMessage.equals(msg)) {
      reader = new ConsoleReader(dispatcher);
      reader.start();
      if (getParameter("hasTranslator", "no").equals("yes")) {
        tr = (Translator) dispatcher.getResourceManager().resourceAcquire(
            Translator.class.getName());
        final Message m = dispatcher.getNewMessage();
        TranslatorGetTranslationMessage.setup(m, "translator-server",
            getObjectName(), msg.getId());
        TranslatorGetTranslationMessage.setLanguage(m, getParameter("language",
            "ru"));
        dispatcher.send(m);
      }
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
   * �����.
   * @param type ������� ������
   * @return ��� ��������
   */
  public final int cleanUp(final int type) {
    logger.finest("Cleaning up...");
    if (reader != null) {
    	try {
			System.in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      reader.exit();
    }
    return 0;
  }

  /**
   * ����������� ������� � �������� ���������� �������.
   * @param id �����
   */
  public ConsoleObject() {
    super(NAME, FULLNAME, VERSION, COPYRIGHT);
  }

  /**
   * ������� ������ ��������.
   * @return ������ ��������
   */
  public final String[] getProviding() {
    String[] res = { NAME };
    return res;
  }

  /**
   * ������� ������ ������������.
   * @return ������ ������������
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