package com.novel.stdobj.console;

import com.novel.odisp.common.PollingODObject;
import com.novel.odisp.common.Message;
/** ������ ODISP ����������� ���������� ��������� ������� � ���������.
* @author �������� �. ��������
* @author (C) 2003, ��� "�����-��"
* @version $Id: ConsoleObject.java,v 1.14 2004/03/17 11:58:06 dron Exp $
*/
public class ConsoleObject extends PollingODObject {
  /** ����� �������� ���� � �������. */
  private ConsoleReader reader;
  /** ���������� �������� ���������.
   * @param msg ���������
   */
  public final void handleMessage(final Message msg) {
    logger.finest("console object -- processing " + msg);
    if (msg.getAction().equals("od_object_loaded")) {
      reader = new ConsoleReader(getObjectName(), dispatcher, logger);
      reader.start();
    } else if (msg.getAction().equals("od_cleanup")) {
      cleanUp(0);
    } else {
      System.out.println("Received:");
      System.out.println(msg.toString());
      if (msg.getFieldsCount() > 0) {

	System.out.println("Fields dump:");
      }
      for (int i = 0; i < msg.getFieldsCount(); i++) {
	System.out.print(i + ":");
	System.out.println(msg.getField("" + i));
      }
    }
    return;
  }
  /** �����.
   * @param type ������� ������
   * @return ��� ��������
   */
  public final int cleanUp(final int type) {
    logger.finest("Cleaning up...");
    if (reader != null) {
      reader.exit();
    }
    doExit = true;
    return 0;
  }
  /** ����������� ������� � �������� ���������� �������.
   * @param id �����
   */
  public ConsoleObject(final Integer id) {
    super("console" + id);
  }
  /** ������� ������ ��������.
   * @return ������ ��������
   */
  public final String[] getProviding() {
    String[] res = {"console"};
    return res;
  }
  /** ������� ������ ������������.
   * @return ������ ������������
   */
  public final String[] getDepends() {
    String[] res = {
      "stddispatcher"
    };
    return res;
  }
}
