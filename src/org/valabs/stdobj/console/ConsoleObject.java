package org.valabs.stdobj.console;

import java.util.ArrayList;
import java.util.Enumeration;

import org.valabs.stdmsg.ModuleAboutMessage;
import org.valabs.stdmsg.ModuleAboutReplyMessage;
import org.valabs.stdmsg.ModuleStatusMessage;
import org.valabs.stdmsg.ModuleStatusReplyMessage;
import org.valabs.stdmsg.TranslatorGetTranslationMessage;
import org.valabs.stdmsg.TranslatorGetTranslationReplyMessage;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.StandartODObject;
import org.valabs.stdmsg.ODAcquireMessage;
import org.valabs.stdmsg.ODCleanupMessage;
import org.valabs.stdmsg.ODObjectLoadedMessage;
import org.valabs.stdmsg.ODResourceAcquiredMessage;
import org.valabs.stdobj.translator.Translator;

/** ������ ODISP ����������� ���������� ��������� ������� � ���������.
 * 
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
 * @author (C) 2003-2004 <a href="mailto:dron@novel-il.ru">������ �. �������</a>
 * @version $Id: ConsoleObject.java,v 1.23 2004/08/23 07:42:37 valeks Exp $
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

  /** ���������� �������� ���������.
   * 
   * @param msg ���������
   */
  public final void handleMessage(final Message msg) {
    logger.finest("console object -- processing " + msg);
    if (ODObjectLoadedMessage.equals(msg)) {
      reader = new ConsoleReader(getObjectName(), dispatcher, logger);
      reader.start();
      if (getParameter("hasTranslator", "no").equals("yes")) {
      	Message m = dispatcher.getNewMessage();
      	ODAcquireMessage.setup(m, getObjectName(), msg.getId());
      	ODAcquireMessage.setResourceName(m, Translator.class.getName());
      	dispatcher.send(m);
      }
    } else if (ODCleanupMessage.equals(msg)) {
      cleanUp(0);
    } else if (ODResourceAcquiredMessage.equals(msg)) {
    	tr = (Translator) ODResourceAcquiredMessage.getResource(msg);
    	Message m = dispatcher.getNewMessage();
    	TranslatorGetTranslationMessage.setup(m, "translator-server", getObjectName(), msg.getId());
    	TranslatorGetTranslationMessage.setLanguage(m, getParameter("language", "ru"));
    	dispatcher.send(m);
    } else if (TranslatorGetTranslationReplyMessage.equals(msg)) {
    	tr.putAll(TranslatorGetTranslationReplyMessage.getTranslation(msg));
    	doTranslation = true;
    	logger.fine("Console translation enabled");
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
    	ModuleStatusReplyMessage.setRunningState(m, "noerror");
    	ModuleStatusReplyMessage.setRunningTasks(m, new ArrayList());
    	ModuleStatusReplyMessage.setCompletedTasks(m, new ArrayList());
    	ModuleStatusReplyMessage.setFailedTasks(m, new ArrayList());
    	dispatcher.send(m);
    } else {
      System.out.println("Received:");
      String msgToString = msg.toString(true);
      if (doTranslation) {
      	Enumeration e = tr.keys();
      	while (e.hasMoreElements()) {
			String element = (String) e.nextElement();
			String replacement = tr.translate(element, element);
			msgToString = msgToString.replaceAll(element, replacement + "[" + element + "]");
		}
      }
      System.out.println(msgToString);
    }
    return;
  }
  
  /** �����.
   * 
   * @param type ������� ������
   * @return ��� ��������
   */
  public final int cleanUp(final int type) {
    logger.finest("Cleaning up...");
    if (reader != null) {
      reader.exit();
    }
    return 0;
  }
  
  /** ����������� ������� � �������� ���������� �������.
   * 
   * @param id �����
   */
  public ConsoleObject(final Integer id) {
    super(NAME + id);
  }
  
  /** ������� ������ ��������.
   * 
   * @return ������ ��������
   */
  public final String[] getProviding() {
    String[] res = { NAME };
    return res;
  }
  
  /** ������� ������ ������������.
   * 
   * @return ������ ������������
   */
  public final String[] getDepends() {
  	if (getParameter("hasTranslator", "no").equals("no")) {
  		String[] res = { "dispatcher" };
  		return res;
  	} else {
  		String[] res = {
  				"dispatcher", 
  				"translator-server", 
				Translator.class.getName(), 
		};
  		return res;
  	}
  }
}
