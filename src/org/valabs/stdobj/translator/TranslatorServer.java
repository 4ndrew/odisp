package com.novel.stdobj.translator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.novel.nms.messages.ModuleAboutMessage;
import com.novel.nms.messages.ModuleAboutReplyMessage;
import com.novel.nms.messages.ModuleStatusMessage;
import com.novel.nms.messages.ModuleStatusReplyMessage;
import com.novel.nms.messages.TranslatorGetTranslationMessage;
import com.novel.nms.messages.TranslatorGetTranslationReplyMessage;
import com.novel.odisp.common.Message;
import com.novel.odisp.common.MessageHandler;
import com.novel.odisp.common.StandartODObject;

/**
 * @author valeks
 * @author (C) 2004 НПП "Новел-ИЛ"
 * @version $Id: TranslatorServer.java,v 1.1 2004/08/20 09:05:51 valeks Exp $
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
		super(NAME + id);
	}

	public void messageReceived(final Message msg) {
		if (ModuleAboutMessage.equals(msg)) {
			Message m = dispatcher.getNewMessage();
			ModuleAboutReplyMessage.setup(m, msg.getOrigin(), getObjectName(),
					msg.getId());
			ModuleAboutReplyMessage.initAll(m, VERSION, null, COPYRIGHT,
					FULL_NAME);
			dispatcher.send(m);
		} else if (ModuleStatusMessage.equals(msg)) {
			Message m = dispatcher.getNewMessage();
			ModuleStatusReplyMessage.setup(m, msg.getOrigin(), getObjectName(),
					msg.getId());
			List dumb = new ArrayList();
			ModuleStatusReplyMessage.initAll(m, dumb, dumb, dumb, "noerror");
		} else if (TranslatorGetTranslationMessage.equals(msg)) { 
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
			TranslatorGetTranslationReplyMessage.initAll(m, result);
			dispatcher.send(m);
		}
	}

	public void registerHandlers() {
		addHandler(ModuleAboutMessage.NAME, this);
		addHandler(ModuleStatusMessage.NAME, this);
		addHandler(TranslatorGetTranslationMessage.NAME, this);
	}
	/**
	 * @see com.novel.odisp.common.ODObject#getDepends()
	 */
	public String[] getDepends() {
		String[] depends = {"dispatcher"};
		return depends;
	}

	/**
	 * @see com.novel.odisp.common.ODObject#getProviding()
	 */
	public String[] getProviding() {
		String[] providing = {NAME,};
		return providing;
	}

}