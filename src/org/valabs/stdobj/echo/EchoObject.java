package com.novel.stdobj.echo;

import com.novel.odisp.common.CallbackODObject;
import com.novel.odisp.common.Message;
import com.novel.odisp.common.MessageHandler;

/** ���������� ODISP ������ ����������� ������������ �� ���������� ���������.
* @author �������� �. ��������
* @author (C) 2003, ��� "�����-��"
* @version $Id: EchoObject.java,v 1.12 2004/04/02 09:54:49 valeks Exp $
*/
public class EchoObject extends CallbackODObject {
  /** ����������� ������������. */
  protected final void registerHandlers() {
    addHandler("od_cleanup", new MessageHandler() {
	public final void messageReceived(final Message msg) {
	  cleanUp(((Integer) msg.getField("0")).intValue());
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
