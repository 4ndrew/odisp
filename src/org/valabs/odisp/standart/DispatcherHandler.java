package com.novel.odisp.standart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.novel.nms.messages.ModuleAboutMessage;
import com.novel.nms.messages.ModuleAboutReplyMessage;
import com.novel.nms.messages.ModuleStatusMessage;
import com.novel.nms.messages.ModuleStatusReplyMessage;
import com.novel.odisp.common.Message;
import com.novel.odisp.common.MessageHandler;
import com.novel.odisp.common.ObjectManager;
import com.novel.odisp.common.ResourceManager;
import com.novel.odisp.common.StandartODObject;
import com.novel.stdmsg.ODAcquireMessage;
import com.novel.stdmsg.ODAddProviderMessage;
import com.novel.stdmsg.ODGetProvidingMessage;
import com.novel.stdmsg.ODGetProvidingReplyMessage;
import com.novel.stdmsg.ODReleaseMessage;
import com.novel.stdmsg.ODRemoveDepMessage;
import com.novel.stdmsg.ODRemoveProviderMessage;
import com.novel.stdmsg.ODShutdownMessage;

/**
 * ���������� ��������� ���������� ODISP.
 * 
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: DispatcherHandler.java,v 1.27 2004/08/18 12:48:40 valeks Exp $
 */

public class DispatcherHandler extends StandartODObject {
    /** ����� heart-beat ����������. */
    private Thread runThread;

    /** ������. */
    private Logger log = Logger
            .getLogger("com.novel.odisp.standart.DispatcherHandler");

    /** �������� ��������. */
    private ObjectManager oman;

    /** �������� ��������. */
    private ResourceManager rman;

    /** ��� �������. */
    private String name = "stddispatcher";

    /** ������ ������. */
    private String VERSION = "0.1.0";

    /** �������������� ���������� � ������. */
    private String COPYRIGHT = "(C) 2003-2004 Valentin A. Alekseev, Andrew A. Porohin";

    /**
     * ������� ������ ��������.
     * 
     * @return ������ ��������
     */
    public final String[] getProviding() {
        String[] res = { "dispatcher" };
        return res;
    }

    /**
     * ������� ������ ������������.
     * 
     * @return ������ ������������
     */
    public final String[] getDepends() {
        String[] res = {};
        return res;
    }

    /** ���������������� ����������� ���������. */
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
                // ��������
                oman.unloadObject(getObjectName(), exitCode);
                runThread.interrupt();
            }
        });
        addHandler(ODAcquireMessage.NAME, new MessageHandler() {
            public final void messageReceived(final Message msg) {
                if (msg.getFieldsCount() > 0) {
                    rman.acquireRequest(msg);
                }
            }
        });
        addHandler(ODReleaseMessage.NAME, new MessageHandler() {
            public final void messageReceived(final Message msg) {
                if (msg.getFieldsCount() > 0) {
                    rman.releaseRequest(msg);
                }
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
                /** @todo. �������� � �������������. */
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
                int count = 0;
                while (it.hasNext()) {
                    running.add(it.next());
                }
                ModuleStatusReplyMessage.setRunningState(m, runningState);
                ModuleStatusReplyMessage.setRunningTasks(m, running);
                ModuleStatusReplyMessage.setCompletedTasks(m, new ArrayList());
                ModuleStatusReplyMessage.setFailedTasks(m, failed);
                dispatcher.send(m);
            }
        });
    }

    /** ��������� ���������� ���������.
     * @param msg ��������� ��� ���������
     */
    public final void handleMessage(final Message msg) {
        if (oman == null || rman == null) {
            /* XXX/HACK ��� ��������� ��� ������� ��������� ��� ������
             * ����� ������������� (�� �������� ����������� registerHandlers) �
             * ������������ ���������.
             */
            oman = dispatcher.getObjectManager();
            rman = dispatcher.getResourceManager();
        }
        super.handleMessage(msg);
    }

    /** ����� ������ �� �������.
     * @param type ������� ������
     * @return ��� ��������
     */
    public final int cleanUp(final int type) {
        return 0;
    }

    /** ����������� �������.
     * @param id ���������� ����� �������
     */
    public DispatcherHandler(final Integer id) {
        super("stddispatcher");
    }

    /** ����������� ��������� ������������.
     * @param cfg ������������ �������
     */
    public void setConfiguration(final Map cfg) {
        /* XXX/HACK � ����� ���. 
         * ���������� "����������" ��ԣ� �������� ������ �� runThread �� ����������.
         */
        runThread = (Thread) cfg.get("runthr");
        cfg.remove("runthr");
        super.setConfiguration(cfg);
    }
} // StandartDispatcherHandler
