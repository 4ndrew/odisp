/* ODISP -- Message Oriented Middleware
 * Copyright (C) 2003-2005 Valentin A. Alekseev
 * Copyright (C) 2003-2005 Andrew A. Porohin 
 * 
 * ODISP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.1 of the License.
 * 
 * ODISP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ODISP.  If not, see <http://www.gnu.org/licenses/>.
 */
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
 * @version $Id: ConsoleObject.java,v 1.32 2005/11/20 12:33:03 valeks Exp $
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
    }
    
    String[] res = { "dispatcher", "translator-server",
        Translator.class.getName(), };
    return res;
  }
}