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
 * @version $Id: ODReleaseMessage.java,v 1.8 2004/03/31 17:01:40 valeks Exp $
 */

public class ODReleaseMessage extends StandartMessage {
  /** ���������� ��� ���������. */
  public static final String NAME = "od_release";

  /** ��� �������. */
  private transient String resourceName = "";
  /** ������ ����� � ���������. */
  private static final String NAME_IDX = "0";
  /** ������. */
  private transient Resource resource;
  /** ������ ������� � ���������. */
  private static final String RES_IDX = "1";

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
      return (Resource) getField(RES_IDX);
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
      getContents().clear();
      addField(NAME_IDX, resourceName);
      addField(RES_IDX, resource);
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
