package com.novel.odisp;

import com.novel.odisp.common.CallbackODObject;
import com.novel.odisp.common.MessageHandler;
import com.novel.odisp.common.Message;
import com.novel.odisp.common.ObjectManager;
import com.novel.odisp.common.ResourceManager;
import com.novel.stdmsg.ODReleaseMessage;
import com.novel.stdmsg.ODRemoveDepMessage;
import com.novel.stdmsg.ODAcquireMessage;
import com.novel.stdmsg.ODShutdownMessage;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Set;

/** ���������� ��������� ���������� ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: DispatcherHandler.java,v 1.7 2004/03/18 11:18:50 valeks Exp $
 */

public class StandartDispatcherHandler extends CallbackODObject {
  /** ����� heart-beat ����������. */
  private Thread runThread;
  /** ������. */
  private Logger log = Logger.getLogger("com.novel.odisp.StandartDispatcherHandler");
  /** �������� ��������. */
  private ObjectManager oman;
  /** �������� ��������. */
  private ResourceManager rman;
  /** ��� �������. */
  private String name = "stddispatcher";
  /** ������� ������ ��������.
   * @return ������ ��������
   */
  public final String[] getProviding() {
    String[] res = {
      "stddispatcher"
    };
    return res;
  }
  /** ������� ������ ������������.
   * @return ������ ������������
   */
  public final String[] getDepends() {
    String[] res = {};
    return res;
  }
  /** ���������������� ����������� ���������. */
  protected final void registerHandlers() {
    oman = dispatcher.getObjectManager();
    rman = dispatcher.getResourceManager();
    addHandler("od_set_run_thread", new MessageHandler() {
	public final void messageReceived(final Message msg) {
	  if (msg.getOrigin().equals("G0D")) {
	    // ��� ������ 0xdeadbeaf
	    runThread = (Thread) msg.getField(0);
	  }
	}
    });
    addHandler("od_unload_object", new MessageHandler() {
	public final void messageReceived(final Message msg) {
	  if (msg.getFieldsCount() != 1) {
	    return;
	  }
	  String objname = (String) msg.getField(0);
	  oman.unloadObject(objname, 1);
	}
      });
    addHandler("od_load_object", new MessageHandler() {
	public final void messageReceived(final Message msg) {
	  if (msg.getFieldsCount() != 1) {
	    return;
	  }
	  String objname = (String) msg.getField(0);
	  oman.loadObject(objname);
	  oman.loadPending();
	}
      });
    addHandler(ODShutdownMessage.NAME, new MessageHandler() {
	public final void messageReceived(final Message msg) {
	  int exitCode = 0;
	  log.info(toString() + " shutting down...");
	  if (msg.getFieldsCount() == 1) {
	    exitCode = ((Integer) msg.getField(0)).intValue();
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
    addHandler("od_list_objects", new MessageHandler() {
	public final void messageReceived(final Message msg) {
	  Message m = dispatcher.getNewMessage("object_list", msg.getOrigin(), getObjectName(), msg.getId());
	  List reply = new ArrayList();
	  Set objs = oman.getObjects().keySet();
	  Iterator it = objs.iterator();
	  while (it.hasNext()) {
	    String objName = (String) it.next();
	    objName+= oman.getBlockedState(objName) > 0 ? ":blocked" : "";
	    reply.add(objName);
	  }
	  m.addField(reply);
	  m.setRoutable(false);
	  dispatcher.send(m);
	}
      });
    addHandler("od_list_resources", new MessageHandler() {
	public final void messageReceived(final Message msg) {
	  Message m = dispatcher.getNewMessage("resource_list", msg.getOrigin(), getObjectName(), msg.getId());
	  List resStat = rman.statRequest();
	  Iterator it = resStat.iterator();
	  while (it.hasNext()) {
	    m.addField(it.next());
	  }
	  dispatcher.send(m);
	}
      });
    addHandler(ODRemoveDepMessage.NAME, new MessageHandler() {
	public final void messageReceived(final Message msg) {
	  if (msg.getFieldsCount() != 1) {
	    return;
	  }
	  ObjectEntry oe = (ObjectEntry) oman.getObjects().get(msg.getOrigin());
	  oe.removeDepend((String) msg.getField(0));
	}
      });
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
  public StandartDispatcherHandler(final Integer id) {
    super("stddispatcher");
  }
} // StandartDispatcherHandler
