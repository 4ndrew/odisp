package org.valabs.stdobj.echo;

import java.util.HashMap;
import java.util.Map;

import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;
import org.valabs.odisp.common.StandartODObject;

/** ���������� ODISP ������ ����������� ������������ �� ���������� ���������.
* @author (C) 2003 <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
* @version $Id: EchoObject.java,v 1.20 2005/01/26 13:22:29 valeks Exp $
*/
public class EchoObject extends StandartODObject {
  private int replyCount = 0;
  /** ����������� ������������. */
  protected final void registerHandlers() {
    addHandler("echo", new MessageHandler() {
      public final void messageReceived(final Message msg) {
        logger.fine("Echo message received");
        Message m = dispatcher.getNewMessage("echo_reply", msg.getOrigin(), getObjectName(), msg.getId());
        m.addField("replyCount", new Integer(replyCount++));
        m.setCorrect(true);
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
  public EchoObject() {
    super("echo", "Echo object", "0.1.0", "(C) 2003-2005 Valentin A. Alekseev");
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
      "dispatcher"
    };
    return res;
  }
  
  public Map exportState() {
    Map result = new HashMap();
    result.put("replyCount", new Integer(replyCount));
    return result;
  }
  
  public void importState(Map state) {
    if (state != null) {
      replyCount = ((Integer) state.get("replyCount")).intValue();
      logger.fine("Old replyCount [" + replyCount + "] restored.");
    }
  }
}
