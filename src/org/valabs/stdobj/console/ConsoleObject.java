package com.novel.stdobj.console;

import com.novel.odisp.common.Message;
import com.novel.odisp.common.StandartODObject;

/** ������ ODISP ����������� ���������� ��������� ������� � ���������.
 * 
 * @author <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
 * @author <a href="mailto:dron@novel-il.ru">������ �. �������</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ConsoleObject.java,v 1.19 2004/06/25 13:15:31 dron Exp $
 */
public class ConsoleObject extends StandartODObject {
  /** ��� ������� */
  public final static String NAME = "console"; 
  /** ����� �������� ���� � �������. */
  private ConsoleReader reader;

  /** ���������� �������� ���������.
   * 
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
      System.out.println(msg.toString(true));
    }
    return;
  }
  
  /** �����.
   * 
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
   * 
   * @param id �����
   */
  public ConsoleObject(final Integer id) {
    super(NAME + id);
  }
  
  /** ������� ������ ��������.
   * 
   * @return ������ ��������
   */
  public final String[] getProviding() {
    String[] res = { NAME };
    return res;
  }
  
  /** ������� ������ ������������.
   * 
   * @return ������ ������������
   */
  public final String[] getDepends() {
    String[] res = { "dispatcher" };
    return res;
  }
}
