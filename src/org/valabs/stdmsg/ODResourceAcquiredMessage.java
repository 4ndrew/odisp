package com.novel.stdmsg;

import com.novel.odisp.common.Resource;

/** ����� �� ������ � ������� �������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODResourceAcquiredMessage.java,v 1.8 2004/01/16 14:31:57 valeks Exp $
 */
public class ODResourceAcquiredMessage extends StandartMessage {
  /** ���������� ��� ���������. */
  public static final String NAME = "od_resource_acquired";

  /** ��� �������. */
  private transient String resourceName = "";
  /** ������ ����� �������. */
  private static final int RESNAME_IDX = 0;
  /** ������. */
  private transient Resource resource;
  /** ������ ����� �������. */
  private static final int RES_IDX = 0;

  /** ����������� ���������.
   * @param destination ���������� ���������
   * @param replyId ��������� �� ������� ������������ �����
   */
  public ODResourceAcquiredMessage(final String destination,
				   final int replyId) {
    super("od_resource_acquired", destination, "stddispatcher", replyId);
  }
  /** ������� ��� ������ �������.
   * @return ��� ������
   */
  public final String getResourceName() {
    if (isCE()) {
      return (String) getField(RESNAME_IDX);
    }
    return resourceName;
  }

  /** ���������� ��� ������ �������.
   * @param newResourceName ����� ��� ������
   * @return ������ �� ���������
   */
  public final ODResourceAcquiredMessage setResourceName
    (final String newResourceName) {
    resourceName = newResourceName;
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
  /** ���������� ������ �� ������.
   * @param newResource ����� �������� ������
   * @return ������ �� ���������
   */
  public final ODResourceAcquiredMessage setResource
    (final Resource newResource) {
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
    }
    return isCE();
  }
} // ODResourceAcquiredMessage
