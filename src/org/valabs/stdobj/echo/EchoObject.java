package org.valabs.stdobj.echo;

import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;
import org.valabs.odisp.common.StandartODObject;
import org.valabs.stdmsg.ODCleanupMessage;

/** ���������� ODISP ������ ����������� ������������ �� ���������� ���������.
* @author (C) 2003 <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
* @version $Id: EchoObject.java,v 1.18 2005/01/25 19:03:34 valeks Exp $
*/
public class EchoObject extends StandartODObject {
  /** ����������� ������������. */
  protected final void registerHandlers() {
    addHandler(ODCleanupMessage.NAME, new MessageHandler() {
	public final void messageReceived(final Message msg) {
	  cleanUp(ODCleanupMessage.getReason(msg).intValue());
	}
      });
    addHandler("echo", new MessageHandler() {
	public final void messageReceived(final Message msg) {
	  Message m
	    = dispatcher.getNewMessage("echo_reply", msg.getOrigin(), getObjectName(), msg.getId());
	  for (int i = 0; i < msg.getFieldsCount(); i++) {
	    m.addField("" + i, msg.getField("" + i));
	  }
	  dispatcher.send(m);
	}
      });
  }
  /** ����� �� �������.
   * @param type ��� ������
   * @return ��� ������
   */
  public final int cleanUp(final int type) {
    return 0;
  }

  /** �������� echo-�������.
   * @param id ������ �������
   */
  public EchoObject(final Integer id) {
    super("echo" + id, "Echo object", "0.1.0", "(C) 2003-2005 Valentin A. Alekseev");
  }
  /** �������� ������ ��������������� ��������.
   * @return ������ ��������
   */
  public final String[] getProviding() {
    String[] res = {
      "echo"
    };
    return res;
  }
  /** �������� ������ ������������.
   * @return ������ ������������
   */
  public final String[] getDepends() {
    String[] res = {
      "dispatcher",
      "log"
    };
    return res;
  }
}
