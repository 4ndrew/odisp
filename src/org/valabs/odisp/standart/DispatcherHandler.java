package org.valabs.odisp.standart;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.valabs.stdmsg.ModuleAboutMessage;
import org.valabs.stdmsg.ModuleAboutReplyMessage;
import org.valabs.stdmsg.ModuleStatusMessage;
import org.valabs.stdmsg.ModuleStatusReplyMessage;
import org.valabs.stdmsg.ODAddProviderMessage;
import org.valabs.stdmsg.ODGetProvidingMessage;
import org.valabs.stdmsg.ODGetProvidingReplyMessage;
import org.valabs.stdmsg.ODRemoveDepMessage;
import org.valabs.stdmsg.ODRemoveProviderMessage;
import org.valabs.stdmsg.ODShutdownMessage;

/**
 * Обработчик сообщений диспетчера ODISP.
 * 
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: DispatcherHandler.java,v 1.33 2005/01/24 12:57:59 valeks Exp $
 */

class DispatcherHandler extends StandartODObject {
    /** Нитка heart-beat диспетчера. */
    private Thread runThread;

    /** Журнал. */
    private Logger log = Logger
            .getLogger("org.valabs.odisp.standart.DispatcherHandler");

    /** Менеджер объектов. */
    private ObjectManager oman;

    /** Менеджер ресурсов. */
    private ResourceManager rman;

    /** Имя объекта. */
    private static String NAME = "stddispatcher";

    /** Версия модуля. */
    private String VERSION = "0.1.0";

    /** Дополнительная информация о модуле. */
    private String COPYRIGHT = "(C) 2003-2004 Valentin A. Alekseev, Andrew A. Porohin";

    /**
     * Вернуть список сервисов.
     * 
     * @return список сервисов
     */
    public final String[] getProviding() {
        String[] res = { "dispatcher", NAME };
        return res;
    }

    /**
     * Вернуть список зависимостей.
     * 
     * @return список зависимостей
     */
    public final String[] getDepends() {
        String[] res = {};
        return res;
    }

    /** Зарегистрировать обработчики сообщений. */
    protected final void registerHandlers() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.err.println("Running shutdown hook.");
                oman.unloadObject(getObjectName(), -1);
                runThread.interrupt();
            }
        });
        addHandler("od_unload_object", new MessageHandler() {
            public final void messageReceived(final Message msg) {
                if (msg.getFieldsCount() != 1) {
                    return;
                }
                String objname = (String) msg.getField("0");
                oman.unloadObject(objname, 1);
            }
        });
        addHandler("od_load_object", new MessageHandler() {
            public final void messageReceived(final Message msg) {
                if (msg.getFieldsCount() != 1) {
                    return;
                }
                String objname = (String) msg.getField("0");
                oman.loadObject(objname, null);
                oman.loadPending();
            }
        });
        addHandler(ODShutdownMessage.NAME, new MessageHandler() {
            public final void messageReceived(final Message msg) {
                int exitCode = 0;
                log.info(toString() + " shutting down...");
                if (msg.getFieldsCount() == 1) {
                    exitCode = ODShutdownMessage.getExitCode(msg).intValue();
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
                ObjectEntry oe = (ObjectEntry) oman.getObjects().get(
                        msg.getOrigin());
                oe.removeDepend(ODRemoveDepMessage.getDepName(msg));
            }
        });
        addHandler(ODGetProvidingMessage.NAME, new MessageHandler() {
            public final void messageReceived(final Message msg) {
                Message m = dispatcher.getNewMessage();
                ODGetProvidingReplyMessage.setup(m, msg.getOrigin(), msg
                        .getId());
                ODGetProvidingReplyMessage.setProvidingList(m, oman
                        .getProviding());
                dispatcher.send(m);
            }
        });
        addHandler(ODAddProviderMessage.NAME, new MessageHandler() {
            public final void messageReceived(final Message msg) {
                String service = ODAddProviderMessage.getServiceName(msg);
                oman.addProvider(service, msg.getOrigin());
            }
        });
        addHandler(ODRemoveProviderMessage.NAME, new MessageHandler() {
            public final void messageReceived(final Message msg) {
                String service = ODRemoveProviderMessage.getServiceName(msg);
                oman.removeProvider(service, msg.getOrigin());
            }
        });
        addHandler(ModuleAboutMessage.NAME, new MessageHandler() {
            public final void messageReceived(final Message msg) {
                Message m = dispatcher.getNewMessage();
                ModuleAboutReplyMessage.setup(m, msg.getOrigin(),
                        getObjectName(), msg.getId());
                ModuleAboutReplyMessage.setName(m,
                        "Standart Object Dispatcher Core");
                ModuleAboutReplyMessage.setVersion(m, VERSION);
                ModuleAboutReplyMessage.setCopyright(m, COPYRIGHT);
                dispatcher.send(m);
            }
        });
        addHandler(ModuleStatusMessage.NAME, new MessageHandler() {
            public final void messageReceived(final Message msg) {
                /** @todo. проблемы с руссификацией. */
                Message m = dispatcher.getNewMessage();
                ModuleStatusReplyMessage.setup(m, msg.getOrigin(),
                        getObjectName(), msg.getId());
                String runningState = "No error";
                List running = new ArrayList();
                List failed = new ArrayList();
                Set objs = oman.getObjects().keySet();
                Iterator it = objs.iterator();
                while (it.hasNext()) {
                    String objName = (String) it.next();
                    ObjectEntry oe = (ObjectEntry) oman.getObjects().get(
                            objName);
                    if (oe.isLoaded()) {
                        running.add(objName);
                    } else {
                        failed.add(objName + " not loaded. Dependencies not met: " + Arrays.asList(oe.getDepends()));
                        runningState = "Warning. Not all pre-requested objects are loaded. Some dependencies aren't met.";
                    }
                }
                List resStat = rman.statRequest();
                it = resStat.iterator();
                while (it.hasNext()) {
                    running.add(it.next().toString());
                }
                ModuleStatusReplyMessage.setRunningState(m, runningState);
                ModuleStatusReplyMessage.setRunningTasks(m, running);
                ModuleStatusReplyMessage.setCompletedTasks(m, new ArrayList());
                ModuleStatusReplyMessage.setFailedTasks(m, failed);
                dispatcher.send(m);
            }
        });
    }

    /** Обработка пришедшего сообщения.
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

    /** Точка выхода из объекта.
     * @param type признак выхода
     * @return код возврата
     */
    public final int cleanUp(final int type) {
        return type;
    }

    /** Конструктор объекта.
     * @param id порядковый номер объекта
     */
    public DispatcherHandler(final Integer id) {
        super(NAME + id);
    }

    /** Специальная обработка конфигурации.
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
} // StandartDispatcherHandler
