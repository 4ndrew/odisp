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
package org.valabs.stdobj.translator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;
import org.valabs.odisp.common.StandartODObject;
import org.valabs.stdmsg.ModuleAboutMessage;
import org.valabs.stdmsg.ModuleStatusMessage;
import org.valabs.stdmsg.TranslatorGetTranslationMessage;
import org.valabs.stdmsg.TranslatorGetTranslationReplyMessage;

/**
 * @author <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>
 * @version $Id: TranslatorServer.java,v 1.11 2005/11/20 12:33:03 valeks Exp $
 */
public class TranslatorServer extends StandartODObject
		implements
			MessageHandler {
	/** Имя объекта. */
	public static String NAME = "translator-server";
	/** Полное имя объекта. */
	private static String FULL_NAME = "Language Translation Server";
	/** Версия объекта. */
	private static String VERSION = "0.1.0";
	/** Авторство. */
	private static String COPYRIGHT = "(C) Valentin A. Alekseev";

	public TranslatorServer() {
		super(NAME, FULL_NAME, VERSION, COPYRIGHT);
	}

	public void messageReceived(final Message msg) {
	  if (TranslatorGetTranslationMessage.equals(msg)) { 
			String language = TranslatorGetTranslationMessage.getLanguage(msg);
			String encoding = TranslatorGetTranslationMessage.getEncoding(msg);
			String packagePath = getParameter("catalogueroot", "/resources/language")
					+ "/" + language + (encoding != null ? "." + encoding : "");
			Properties result = new Properties();
	    BufferedReader inSettings = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(packagePath + "/settings")));
	    try {
	      String inLine = null;
	      do {
	        inLine = inSettings.readLine();
	        if (inLine != null) {
	          try {
	            result.load(getClass().getResourceAsStream(packagePath + "/" + inLine));
	          } catch (Exception e) {
	            logger.warning("Unable to load resource file " + inLine);
	          }
	        }
	      } while (inLine != null);
	    } catch (IOException e) { /* игнорируется */ }
			Message m = dispatcher.getNewMessage();
			TranslatorGetTranslationReplyMessage.setup(m, msg.getOrigin(),
					getObjectName(), msg.getId());
			TranslatorGetTranslationReplyMessage.setTranslation(m, result);
			dispatcher.send(m);
		}
	}

	public void registerHandlers() {
		addHandler(ModuleAboutMessage.NAME, this);
		addHandler(ModuleStatusMessage.NAME, this);
		addHandler(TranslatorGetTranslationMessage.NAME, this);
	}
	/**
	 * @see org.valabs.odisp.common.ODObject#getDepends()
	 */
	public String[] getDepends() {
		String[] depends = {"dispatcher"};
		return depends;
	}

	/**
	 * @see org.valabs.odisp.common.ODObject#getProviding()
	 */
	public String[] getProviding() {
		String[] providing = {NAME,};
		return providing;
	}

}
