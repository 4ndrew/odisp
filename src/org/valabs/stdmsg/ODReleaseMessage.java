package com.novel.stdmsg;

import com.novel.odisp.common.Resource;

/** ������ ���������� �� ������������� �������.
 * <p>������ ������ ��������� ������� 1 �������� - ��� ���������� �������.
 * �������������� �������������� �������� (�� ��������� - ����) ����������
 * ����� �� ������ �������� ������������� �� ��� ��� ���� �� �� ��������� ������.</p>
 * <p><b>��������</b>: ������� ���������� ������������ ���������� �� ��������� ��������� 
 * ����������� ���� �� ����������� ������, ������� ������������� � ����������� ��� ���.
 * ��� ����, ��� �� �������� ������������� ���������, ������������� ������������ ����������� 
 * ������ �� ����� ������ ������� � ������������� ������ ����������.</p>
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODReleaseMessage.java,v 1.4 2003/12/15 14:02:43 valeks Exp $
 */

public class ODReleaseMessage extends StandartMessage {
  /** ���������� ��� ���������*/
  public static final String name = "od_object_loaded";

  private transient String resourceName = "";
  private transient Resource resource;

  /** ������� ����� ��������� ���������� � �������� �� ������ �������
   * @param origin �����
   * @param replyTo � ����� �� ��������� No.
   */
  public ODReleaseMessage(String origin, int replyTo) {
    super("od_release", "stddispatcher", origin, replyTo);
  }

  /** ������� ��� ������� ��� �������
   * @return ��� �������
   */
  public String getResourceName() {
    if (ce) { 
      return (String) getField(0);
    }
    return resourceName;
  }

  /** ���������� ��� �������
   * @param newName ����� ���
   * @return ������ �� ������� ���������
   */
  public ODReleaseMessage setResourceName(String newName) {
    resourceName = newName;
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

  /** ���������� ����� �������� ������ �� ������
   * @param newResource ����� �������� ������
   * @return ������ �� ������� ���������
   */
  public ODReleaseMessage setResource(Resource newResource) {
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
      ce = true;
      return true;
    } else {
      return false;
    }
  }

}// ODReleaseMessage
