package com.novel.stdmsg;

import com.novel.odisp.common.Resource;

/** ������ ���������� �� ������������� �������.
 * <p>������ ������ ��������� ������� 1 �������� - ��� ���������� �������.
 * �������������� �������������� �������� (�� ��������� - ����) ����������
 * ����� �� ������ �������� ������������� �� ��� ��� ���� �� �� ���������
 * ������.</p>
 * <p><b>��������</b>: ������� ���������� ������������ ���������� ��
 * ��������� ��������� ����������� ���� �� ����������� ������, �������
 * ������������� � ����������� ��� ���. ��� ����, ��� �� ��������
 * ������������� ���������, ������������� ������������ �����������
 * ������ �� ����� ������ ������� � ������������� ������ ����������.</p>
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODReleaseMessage.java,v 1.7 2004/02/24 00:35:14 valeks Exp $
 */

public class ODReleaseMessage extends StandartMessage {
  /** ���������� ��� ���������. */
  public static final String NAME = "od_release";

  /** ��� �������. */
  private transient String resourceName = "";
  /** ������ ����� � ���������. */
  private static final int NAME_IDX = 0;
  /** ������. */
  private transient Resource resource;
  /** ������ ������� � ���������. */
  private static final int RES_IDX = 0;

  /** ������� ����� ��������� ���������� � �������� �� ������ �������.
   * @param origin �����
   * @param replyTo � ����� �� ��������� No.
   */
  public ODReleaseMessage(final String origin, final int replyTo) {
    super("od_release", "stddispatcher", origin, replyTo);
  }

  /** ������� ��� ������� ��� �������.
   * @return ��� �������
   */
  public final String getResourceName() {
    if (isCE()) {
      return (String) getField(NAME_IDX);
    }
    return resourceName;
  }

  /** ���������� ��� �������.
   * @param newName ����� ���
   * @return ������ �� ������� ���������
   */
  public final ODReleaseMessage setResourceName(final String newName) {
    resourceName = newName;
    return this;
  }

  /** ������� ������ �� ������.
   * @return ������ �� ������
   */
  public final Resource getResource() {
    if (isCE()) {
      return (Resource) getField(1);
    }
    return resource;
  }

  /** ���������� ����� �������� ������ �� ������.
   * @param newResource ����� �������� ������
   * @return ������ �� ������� ���������
   */
  public final ODReleaseMessage setResource(final Resource newResource) {
    resource = newResource;
    return this;
  }

  /** �������� ������������ ���������.
   * @return ���� ������������
   */
  public final boolean isCorrect() {
    if (isCE()) {
      return true;
    }
    if (resourceName != "" && resource != null) {
      getFields().clear();
      addField(resourceName);
      addField(resource);
      setCE(true);
      return true;
    } else {
      return false;
    }
  }
  public final boolean isRoutable() {
    return false;
  }
} // ODReleaseMessage
