package com.novel.odisp;

import com.novel.odisp.common.CallbackODObject;
import com.novel.odisp.common.MessageHandler;
import com.novel.odisp.common.Message;
import com.novel.odisp.common.ObjectManager;
import com.novel.odisp.common.ResourceManager;
import java.util.logging.Logger;
import java.util.ArrayList;

/** Обработчик сообщений диспетчера ODISP.
 * @author (C) 2004 <a href="mailto:valeks@valeks.novel.local">Valentin A. Alekseev</a>
 * @version $Id: DispatcherHandler.java,v 1.1 2004/02/13 13:15:17 valeks Exp $
 */

public class StandartDispatcherHandler extends CallbackODObject {
  private Logger log = Logger.getLogger("com.novel.odisp.StandartDispatcherHandler");
  private ObjectManager oman;
  private ResourceManager rman;
  private int msgId = 0;
  /** Имя объекта. */
  private String name = "stddispatcher";
  /** Вернуть список сервисов.
   * @return список сервисов
   */
  public final String[] getProviding() {
    String[] res = {
      "stddispatcher"
    };
    return res;
  }
  /** Вернуть список зависимостей.
   * @return список зависимостей
   */
  public final String[] getDepends() {
    String[] res = {};
    return res;
  }
  /** Зарегистрировать обработчики сообщений. */
  protected final void registerHandlers() {
    oman = dispatcher.getObjectManager();
    rman = dispatcher.getResourceManager();
    addHandler("unload_object", new MessageHandler() {
	public final void messageReceived(final Message msg) {
	  if (msg.getFieldsCount() != 1) {
	    return;
	  }
	  String objname = (String) msg.getField(0);
	  oman.unloadObject(objname, 1);
	  oman.getObjects().remove(objname);
	}
      });
    addHandler("load_object", new MessageHandler() {
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
  /** Точка выхода из объекта.
   * @param type признак выхода
   * @return код возврата
   */
  public final int cleanUp(final int type) {
    return 0;
  }
  /** Конструктор объекта.
   * @param id порядковый номер объекта
   */
  public StandartDispatcherHandler(final Integer id) {
    super("stddispatcher");
  }
}// StandartDispatcherHandler
