package com.novel.stdobj.racer;

import com.novel.odisp.common.Message;
import com.novel.odisp.common.StandartODObject;
import com.novel.odisp.common.Resource;
import com.novel.stdmsg.ODAcquireMessage;
import com.novel.stdmsg.ODCleanupMessage;
import com.novel.stdmsg.ODObjectLoadedMessage;
import com.novel.stdmsg.ODReleaseMessage;
import com.novel.stdmsg.ODResourceAcquiredMessage;

/** Объект, который пытается воспроизвести быстрые асинхронные запросы на
 * захват и высвобождение ресурса.
 * 
 * @author (C) 2004 <a href="valeks@novel-il.ru">Валентин А. Алексеев</a>
 * @author <a href="dron@novel-il.ru">Андрей А. Порохин</a>
 * @version $Id: Racer.java,v 1.9 2004/06/09 00:28:48 valeks Exp $
 */
public class Racer extends StandartODObject {
  /** Счётчик запросов */
  private int acquireCount = 0;
  /** Флаг: запрошен ли ресурс */
  private Boolean requested = new Boolean(false);

  public Racer(Integer id) {
    super("racer" + id);
  }

  public final void handleMessage(final Message msg) {
    if (ODObjectLoadedMessage.equals(msg)) {
      ODAcquireMessage m = new ODAcquireMessage(getObjectName(), msg.getId());
      m.setResourceName("com.novel.stdobj.simpleconfig.SimpleConfig");

      synchronized (requested) {
        requested = new Boolean(true);
      }

      dispatcher.send(m);
    } else if (msg instanceof ODCleanupMessage) {
      cleanUp(0);
    } else if (msg instanceof ODResourceAcquiredMessage) {
      synchronized (requested) {
        if (!requested.booleanValue()) {
          // Отладка повторного получения сообщения od_resorce_acquared
          logger.warning("-------------------------------------------------------------");
          logger.warning("Resource not requested, but message received.");
          logger.warning("-------------------------------------------------------------");
          System.exit(-1);
        }
        requested = new Boolean(false);
      }

      try {
      	wait((int)Math.random() * 100);
      } catch (Exception e) {}
      ODReleaseMessage m = new ODReleaseMessage(getObjectName(), msg.getId());
      m.setResourceName((String) msg.getField("0"));
      m.setResource((Resource) msg.getField("1"));
      dispatcher.send(m);
      // Это пока не надо.
      //if (acquireCount > 100)
      //  return;
      acquireCount++;
      try {
      	wait((int)Math.random() * 100);
      } catch (Exception e) {}
      ODAcquireMessage m1 = new ODAcquireMessage(getObjectName(), msg.getId());
      m1.setResourceName("com.novel.stdobj.simpleconfig.SimpleConfig");
      synchronized (requested) {
        requested = new Boolean(true);
        dispatcher.send(m1);
      }
    }
  }
  public int cleanUp(int code) {
    return code;
  }
  public String[] getProviding() {
    String[] result = {
      "racer"
    };
    return result;
  }
  public String[] getDepends() {
    String[] result = {
      "dispatcher",
      "com.novel.stdobj.simpleconfig.SimpleConfig"
    };
    return result;
  }
}
