package com.novel.stdmsg;

import com.novel.odisp.common.Resource;

/** ����� �� ������ � ������� �������
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODResourceAcquiredMessage.java,v 1.6 2003/12/15 14:02:43 valeks Exp $
 */
public class ODResourceAcquiredMessage extends StandartMessage {
  /** ���������� ��� ���������*/
  public static final String name = "od_resource_acquired";

  private transient String resourceName = "";
  private transient Resource resource;

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
  public String getResourceName() {
    if (ce) {
      return (String) getField(0);
    }
    return resourceName;
  }

  /** ���������� ��� ������ �������
   * @param newClassName ����� ��� ������
   * @return ������ �� ���������
   */
  public ODResourceAcquiredMessage setResourceName(String newResourceName) {
    resourceName = newResourceName;
    return this;
  }

  /** ������� ������ �� ������
   * @return ������ �� ������
   */
  public Resource getResource() {
    if (ce) {
      return (Resource) getField(1);
    }
    return resource;
  }
  /** ���������� ������ �� ������
   * @param newResource ����� �������� ������
   * @return ������ �� ���������
   */
  public ODResourceAcquiredMessage setResource(Resource newResource) {
    resource = newResource;
    return this;
  }

  public boolean isCorrect() {
    if (ce) {
      return true;
    }
    if (resourceName != "" && resource != null) {
      fields.clear();
      addField(resourceName);
      addField(resource);
      return true;
    } else {
      return false;
    }
  }

}// ODResourceAcquiredMessage
