package com.novel.stdobj.console;

import com.novel.odisp.common.Message;
import com.novel.odisp.common.StandartODObject;
/** ������ ODISP ����������� ���������� ��������� ������� � ���������.
* @author �������� �. ��������
* @author (C) 2003, ��� "�����-��"
* @version $Id: ConsoleObject.java,v 1.18 2004/05/13 09:25:46 valeks Exp $
*/
public class ConsoleObject extends StandartODObject {
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
      "dispatcher"
    };
    return res;
  }
}
