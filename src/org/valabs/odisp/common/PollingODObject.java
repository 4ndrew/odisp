package com.novel.odisp.common;

import java.util.regex.Pattern;

/** ������� ����� ����������� �������� ���� ��� ���������
* ���������� ����������� ODISP.
* @author �������� �. ��������
* @author (�) 2003-2004, ��� "�����-��"
* @version $Id: PollingODObject.java,v 1.23 2004/05/12 13:40:50 dron Exp $
*/
public abstract class PollingODObject extends ODObject {
  /** ����������� �������� ODISP ��� �������.
   * @param name ��� �������
   */
  public PollingODObject(final String name) {
    super(name);
  }
  /** ��������� ���������� ��������� � ����.
   * @param msg ���������
   */
  public final void addMessage(final Message msg) {
    if (!Pattern.matches(match, msg.getDestination()) &&
        !Pattern.matches(msg.getDestination(), getObjectName())) {    
      return;    
    }
    handleMessage(msg);
  }
  /** ����� ���������� ��� ������� ������ ������.
   * @param type ������� ������
   * @return ��� ��������
   */
  public int cleanUp(final int type) {
    return 0;
  }
}
