package com.novel.stdobj.echo;

import com.novel.odisp.common.Message;
import com.novel.odisp.common.MessageHandler;
import com.novel.odisp.common.StandartODObject;
import com.novel.stdmsg.ODCleanupMessage;

/** ���������� ODISP ������ ����������� ������������ �� ���������� ���������.
* @author (C) 2003 <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
* @version $Id: EchoObject.java,v 1.16 2004/08/18 12:48:40 valeks Exp $
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
    super("echo" + id);
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
