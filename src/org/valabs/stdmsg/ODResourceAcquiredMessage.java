package com.novel.stdmsg;

import com.novel.odisp.common.Resource;

/** ����� �� ������ � ������� �������
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODResourceAcquiredMessage.java,v 1.4 2003/12/04 09:48:20 valeks Exp $
 */
public class ODResourceAcquiredMessage extends StandartMessage {
  /** ���������� ��� ���������*/
  public static final String name = "od_resource_acquired";
  /** ����������� ���������
   * @param destination ���������� ���������
   * @param replyId ��������� �� ������� ������������ �����
   */
  public ODResourceAcquiredMessage(String destination, int replyId) {
    super("od_resource_acquired", destination, "stddispatcher", replyId);
  }
  /** ������� ��� ������ �������
   * @return ��� ������
   */
  public String getClassName() {
    return (String) getField(0);
  }

  /** ���������� ��� ������ �������
   * @param newClassName ����� ��� ������
   * @return ������ �� ���������
   */
  public ODResourceAcquiredMessage setClassName(String newClassName) {
    fields.add(0, newClassName);
    return this;
  }

  /** ������� ������ �� ������
   * @return ������ �� ������
   */
  public Resource getResource() {
    return (Resource) getField(1);
  }
  /** ���������� ������ �� ������
   * @param newResource ����� �������� ������
   * @return ������ �� ���������
   */
  public ODResourceAcquiredMessage setResource(Resource newResource) {
    fields.add(1, newResource);
    return this;
  }

}// ODResourceAcquiredMessage
