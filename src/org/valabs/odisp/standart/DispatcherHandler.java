package com.novel.odisp;

import com.novel.odisp.common.CallbackODObject;
import com.novel.odisp.common.MessageHandler;
import com.novel.odisp.common.Message;
import com.novel.odisp.common.ObjectManager;
import com.novel.odisp.common.ResourceManager;
import java.util.logging.Logger;
import java.util.ArrayList;

/** ���������� ��������� ���������� ODISP.
 * @author (C) 2004 <a href="mailto:valeks@valeks.novel.local">Valentin A. Alekseev</a>
 * @version $Id: DispatcherHandler.java,v 1.2 2004/02/13 14:09:04 valeks Exp $
 */

public class StandartDispatcherHandler extends CallbackODObject {
  private Thread runThread;
  private Logger log = Logger.getLogger("com.novel.odisp.StandartDispatcherHandler");
  private ObjectManager oman;
  private ResourceManager rman;
  private int msgId = 0;
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
	  runThread = (Thread) msg.getField(0);
	}
    });
    addHandler("od_unload_object", new MessageHandler() {
	public final void messageReceived(final Message msg) {
	  if (msg.getFieldsCount() != 1) {
	    return;
	  }
	  String objname = (String) msg.getField(0);
	  oman.unloadObject(objname, 1);
	  oman.getObjects().remove(objname);
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
    addHandler("od_shutdown", new MessageHandler() {
	public final void messageReceived(final Message msg) {
	  int exitCode = 0;
	  log.info(toString() + " shutting down...");
	  if (msg.getFieldsCount() == 1) {
	    exitCode = ((Integer) msg.getField(0)).intValue();
	  }
	  oman.unloadObject("stddispatcher", exitCode);
	  runThread.interrupt();
	}
      });
    addHandler("od_acquire", new MessageHandler() {
	public final void messageReceived(final Message msg) {
	  if (msg.getFieldsCount() > 0) {
	    rman.acquireRequest(msg);
	  }
	}
      });
    addHandler("od_release", new MessageHandler() {
	public final void messageReceived(final Message msg) {
	  rman.releaseRequest(msg);
	}
      });
    addHandler("od_list_objects", new MessageHandler() {
	public final void messageReceived(final Message msg) {
	  Message m = dispatcher.getNewMessage("object_list", msg.getOrigin(), "stddispatcher", msg.getId());
	  m.addField(new ArrayList(oman.getObjects().keySet()));
	  dispatcher.send(m);
	}
      });
    addHandler("od_list_resources", new MessageHandler() {
	public final void messageReceived(final Message msg) {
	  Message m = dispatcher.getNewMessage("resource_list", msg.getOrigin(), "stddispatcher", msg.getId());
	  m.addField(new ArrayList(rman.getResources().keySet()));
	  dispatcher.send(m);
	}
      });
    addHandler("od_remove_dep", new MessageHandler() {
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
    setDaemon(false);
  }
}// StandartDispatcherHandler
