package org.valabs.stdobj.racer;

import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.Resource;
import org.valabs.odisp.common.StandartODObject;
import org.valabs.stdmsg.ODAcquireMessage;
import org.valabs.stdmsg.ODCleanupMessage;
import org.valabs.stdmsg.ODObjectLoadedMessage;
import org.valabs.stdmsg.ODReleaseMessage;
import org.valabs.stdmsg.ODResourceAcquiredMessage;
import org.valabs.stdobj.translator.Translator;

/** ������, ������� �������� ������������� ������� ����������� ������� ��
 * ������ � ������������� �������.
 * 
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
 * @author (C) 2004 <a href="mailto:dron@novel-il.ru">������ �. �������</a>
 * @version $Id: Racer.java,v 1.18 2004/08/23 07:42:38 valeks Exp $
 */
public class Racer extends StandartODObject {
  /** �ޣ���� �������� */
  private int acquireCount = 0;
  /** ����: �������� �� ������ */
  private Boolean requested = new Boolean(false);

  public Racer(Integer id) {
    super("racer" + id);
  }

  public final void handleMessage(final Message msg) {
    if (ODObjectLoadedMessage.equals(msg)) {
      Message m = dispatcher.getNewMessage();
      ODAcquireMessage.setup(m, getObjectName(), msg.getId());
      ODAcquireMessage.setResourceName(m, Translator.class.getName());

      synchronized (requested) {
        requested = new Boolean(true);
      }

      dispatcher.send(m);
    } else if (ODCleanupMessage.equals(msg)) {
      cleanUp(0);
    } else if (ODResourceAcquiredMessage.equals(msg)) {
      synchronized (requested) {
        if (!requested.booleanValue()) {
          // ������� ���������� ��������� ��������� od_resorce_acquared
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
      Message m = dispatcher.getNewMessage();
      ODReleaseMessage.setup(m, getObjectName(), msg.getId());
      ODReleaseMessage.setResourceName(m, (String) msg.getField("0"));
      ODReleaseMessage.setResource(m, (Resource) msg.getField("1"));
      dispatcher.send(m);
      // ��� ���� �� ����.
      //if (acquireCount > 100)
      //  return;
      acquireCount++;
      try {
      	wait((int)Math.random() * 100);
      } catch (Exception e) {}

      Message m1 = dispatcher.getNewMessage();
      ODAcquireMessage.setup(m1, getObjectName(), msg.getId());
      ODAcquireMessage.setResourceName(m1, Translator.class.getName());
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
      Translator.class.getName()
    };
    return result;
  }
}
