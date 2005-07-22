package org.valabs.odisp.standart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;
import org.valabs.odisp.common.ObjectManager;
import org.valabs.odisp.common.ResourceManager;
import org.valabs.odisp.common.StandartODObject;
import org.valabs.stdmsg.CopyrightGetMessage;
import org.valabs.stdmsg.CopyrightGetReplyMessage;
import org.valabs.stdmsg.ModuleStatusMessage;
import org.valabs.stdmsg.ModuleStatusReplyMessage;
import org.valabs.stdmsg.ODRemoveDepMessage;
import org.valabs.stdmsg.ODShutdownMessage;

/**
 * Обработчик сообщений диспетчера ODISP.
 * 
 * @author (C) 2004-2005 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: DispatcherHandler.java,v 1.42 2005/07/22 13:06:54 dron Exp $
 */

class DispatcherHandler extends StandartODObject {
    /** Нитка heart-beat диспетчера. */
    private Thread runThread;

    /** Журнал. */
    private final static Logger log = Logger
            .getLogger("org.valabs.odisp.standart.DispatcherHandler");

    /** Менеджер объектов. */
    private ObjectManager oman;

    /** Менеджер ресурсов. */
    private ResourceManager rman;

    /** Имя объекта. */
    private static final String NAME = "stddispatcher";
    
    private static final String FULLNAME = "Standart ODISP Dispatcher Core";

    /** Версия модуля. */
    private static final String VERSION = "0.1.0";

    /** Дополнительная информация о модуле. */
    private static final String COPYRIGHT = "(C) 2003-2005 Valentin A. Alekseev, Andrew A. Porohin";

    /**
     * Вернуть список сервисов.
     * 
     * @return список сервисов
     */
    public final String[] getProviding() {
        final String[] res = { "dispatcher", NAME };
        return res;
    }

    /**
     * Вернуть список зависимостей.
     * 
     * @return список зависимостей
     */
    public final String[] getDepends() {
        final String[] res = {};
        return res;
    }

    /**
     * Зарегистрировать обработчики сообщений.
     */
    protected final void registerHandlers() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
              logger.info("Running shutdown hook.");
                oman.unloadObject(getObjectName(), -1);
                runThread.interrupt();
            }
        });
        addHandler(ODShutdownMessage.NAME, new MessageHandler() {
            public final void messageReceived(final Message msg) {
                int exitCode = 0;
                log.info(toString() + " shutting down...");
                if (msg.getFieldsCount() == 1) {
                    exitCode = ((Integer) msg.getField("exitcode")).intValue();
                }
                // харакири
                oman.unloadObject(getObjectName(), exitCode);
                runThread.interrupt();
            }
        });
        addHandler(ODRemoveDepMessage.NAME, new MessageHandler() {
            public final void messageReceived(final Message msg) {
                if (msg.getFieldsCount() != 1) {
                    return;
                }
                final ObjectEntry oe = (ObjectEntry) oman.getObjects().get(
                        msg.getOrigin());
                oe.removeDepend(ODRemoveDepMessage.getDepName(msg));
            }
        });
//        addHandler(ODGetProvidingMessage.NAME, new MessageHandler() {
//            public final void messageReceived(final Message msg) {
//                final Message m = dispatcher.getNewMessage();
//                ODGetProvidingReplyMessage.setup(m, getObjectName(), msg.getOrigin(), msg
//                        .getId());
//                ODGetProvidingReplyMessage.setProvidingList(m, oman
//                        .getProviding());
//                dispatcher.send(m);
//            }
//        });
//        addHandler(ODAddProviderMessage.NAME, new MessageHandler() {
//            public final void messageReceived(final Message msg) {
//                oman.addProvider(ODAddProviderMessage.getServiceName(msg), msg.getOrigin());
//            }
//        });
//        addHandler(ODRemoveProviderMessage.NAME, new MessageHandler() {
//            public final void messageReceived(final Message msg) {
//                oman.removeProvider(ODRemoveProviderMessage.getServiceName(msg), msg.getOrigin());
//            }
//        });
        addHandler(ModuleStatusMessage.NAME, new MessageHandler() {
            public final void messageReceived(final Message msg) {
                /** @todo. проблемы с руссификацией. */
                final Message m = dispatcher.getNewMessage();
                ModuleStatusReplyMessage.setup(m, msg.getOrigin(),
                        getObjectName(), msg.getId());
                String runningState = "No error";
                final List running = new ArrayList();
                final List failed = new ArrayList();
                final Set objs = oman.getObjects().keySet();
                Iterator commonIt = objs.iterator();
                while (commonIt.hasNext()) {
                    final String objName = (String) commonIt.next();
                    final ObjectEntry oe = (ObjectEntry) oman.getObjects().get(objName);
                    if (oe.isLoaded()) {
                        running.add(objName);
                    } else {
                        failed.add(objName + " not loaded. Dependencies not met: " + oe.getDepends());
                        runningState = "Warning. Not all pre-requested objects are loaded. Some dependencies aren't met.";
                    }
                }
                final List resStat = rman.statRequest();
                commonIt = resStat.iterator();
                while (commonIt.hasNext()) {
                    running.add(commonIt.next().toString());
                }
                ModuleStatusReplyMessage.setRunningState(m, runningState);
                ModuleStatusReplyMessage.setRunningTasks(m, running);
                ModuleStatusReplyMessage.setCompletedTasks(m, new ArrayList());
                ModuleStatusReplyMessage.setFailedTasks(m, failed);
                dispatcher.send(m);
            }
        });
        addHandler(CopyrightGetMessage.NAME, new MessageHandler() {
			public void messageReceived(Message msg) {
		    	List result = new ArrayList();
		    	result.add("ODISP MOM (C) 2003-2005 Valentin A. Alekseev <valeks@valabs.spb.ru>, Andrew A. Porohin <@>");
		    	result.add("XLang XML Parser (C) 2004 Valentin A. Alekseev <valeks@valabs.spb.ru>");
		    	result.add("Java UUID Generator (JUG) Copyright (c) 2002-2005 Tatu Saloranta, tatu.saloranta@iki.fi");
		    	Message m = dispatcher.getNewMessage();
		    	CopyrightGetReplyMessage.setup(m, msg.getOrigin(), getObjectName(), msg.getId());
		    	CopyrightGetReplyMessage.setCopyrights(m, result);
		    	dispatcher.send(m);
			}
		});
    }

    /**
     * Обработка пришедшего сообщения.
     * @param msg сообщение для обработки
     */
    public final void handleMessage(final Message msg) {
        if (oman == null || rman == null) {
            /* XXX/HACK это небольшой хак который позволяет нам встать
             * между конструктором (из которого выполняется registerHandlers) и
             * обработчиком сообщений.
             */
            oman = dispatcher.getObjectManager();
            rman = dispatcher.getResourceManager();
        }
        super.handleMessage(msg);
    }

    /**
     * Точка выхода из объекта.
     * 
     * @param type признак выхода
     * @return код возврата
     */
    public final int cleanUp(final int type) {
        return type;
    }

    /**
     * Конструктор объекта.
     */
    public DispatcherHandler() {
        super(NAME, FULLNAME, VERSION, COPYRIGHT);
    }

    /**
     * Специальная обработка конфигурации.
     * 
     * @param cfg конфигурация объекта
     */
    public void setConfiguration(final Map cfg) {
        /* XXX/HACK и опять хак. 
         * Необходимо "правильным" путём передать ссылку на runThread от диспетчера.
         */
        runThread = (Thread) cfg.get("runthr");
        cfg.remove("runthr");
        super.setConfiguration(cfg);
    }
    
    public Map exportState() {
      return new HashMap();
    }
} // StandartDispatcherHandler
