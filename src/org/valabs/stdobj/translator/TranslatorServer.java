package org.valabs.stdobj.translator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.valabs.stdmsg.ModuleAboutMessage;
import org.valabs.stdmsg.ModuleAboutReplyMessage;
import org.valabs.stdmsg.ModuleStatusMessage;
import org.valabs.stdmsg.ModuleStatusReplyMessage;
import org.valabs.stdmsg.TranslatorGetTranslationMessage;
import org.valabs.stdmsg.TranslatorGetTranslationReplyMessage;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;
import org.valabs.odisp.common.StandartODObject;

/**
 * @author valeks
 * @author (C) 2004 НПП "Новел-ИЛ"
 * @version $Id: TranslatorServer.java,v 1.4 2005/01/25 19:03:33 valeks Exp $
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
	private static String COPYRIGHT = "(C) JTT Novel-IL";

	public TranslatorServer(final Integer id) {
		super(NAME + id, FULL_NAME, VERSION, COPYRIGHT);
	}

	public void messageReceived(final Message msg) {
	  if (TranslatorGetTranslationMessage.equals(msg)) { 
			String language = TranslatorGetTranslationMessage.getLanguage(msg);
			String encoding = TranslatorGetTranslationMessage.getEncoding(msg);
			/** @todo. Possible security vulnerability -- directory traversal. */
			File f = new File(getParameter("catalogueroot", "resources"
					+ File.separator + "language")
					+ File.separator
					+ language
					+ (encoding != null ? "." + encoding : ""));
			if (!f.canRead() && !f.isDirectory()) {
				/** TODO: вернуть ошибку. */
				return;
			}
			String[] files = f.list(new FilenameFilter() {
				public boolean accept(final File dir, final String name) {
					return name.endsWith("lng");
				}
			});
			Properties result = new Properties();
			for (int i = 0; i < files.length; i++) {
				try {
					result.load(new FileInputStream(f.getAbsolutePath() + File.separator + files[i]));
				} catch (FileNotFoundException e) {
					// за время пути собачка могла подрасти?!?
					dispatcher.getExceptionHandler().signalException(e);
				} catch (IOException e) {
					dispatcher.getExceptionHandler().signalException(e);
				}
			}
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
