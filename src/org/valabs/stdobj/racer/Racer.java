package com.novel.stdobj.racer;

import com.novel.odisp.common.Message;
import com.novel.odisp.common.PollingODObject;
import com.novel.odisp.common.Resource;
import com.novel.stdmsg.ODAcquireMessage;
import com.novel.stdmsg.ODCleanupMessage;
import com.novel.stdmsg.ODObjectLoadedMessage;
import com.novel.stdmsg.ODReleaseMessage;
import com.novel.stdmsg.ODResourceAcquiredMessage;

/** Объект, который пытается воспроизвести быстрые асинхронные запросы на захват и высвобождение ресурса.
 * @author (C) 2004 <a href="valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: Racer.java,v 1.4 2004/03/31 12:54:48 dron Exp $
 */
public class Racer extends PollingODObject {
  private int acquireCount = 0;
  public Racer(Integer id) {
    super("racer" + id);
  }
  public final void handleMessage(final Message msg) {
    if (msg instanceof ODObjectLoadedMessage) {
      ODAcquireMessage m = new ODAcquireMessage(getObjectName(), msg.getId());
      m.setResourceName("com.novel.stdobj.simpleconfig.SimpleConfig");
      dispatcher.send(m);
    } else if (msg instanceof ODCleanupMessage) {
      cleanUp(0);
    } else if (msg instanceof ODResourceAcquiredMessage) {
      try {
	sleep((int)Math.random() * 100);
      } catch (InterruptedException e) {}
      ODReleaseMessage m = new ODReleaseMessage(getObjectName(), msg.getId());
      m.setResourceName((String) msg.getField("0"));
      m.setResource((Resource) msg.getField("1"));
      dispatcher.send(m);
      if (acquireCount > 100)
	return;
      acquireCount++;
      try {
	sleep((int)Math.random() * 100);
      } catch (InterruptedException e) {}
      ODAcquireMessage m1 = new ODAcquireMessage(getObjectName(), msg.getId());
      m1.setResourceName("com.novel.stdobj.simpleconfig.SimpleConfig");
      dispatcher.send(m1);
    }
  }
  public int cleanUp(int code) {
    doExit = true;
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
      "stddispatcher",
      "com.novel.stdobj.simpleconfig.SimpleConfig"
    };
    return result;
  }
}
