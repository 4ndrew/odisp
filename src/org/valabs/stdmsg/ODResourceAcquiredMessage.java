package com.novel.stdmsg;

import com.novel.odisp.common.Resource;

/** ����� �� ������ � ������� �������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODResourceAcquiredMessage.java,v 1.10 2004/03/31 17:01:40 valeks Exp $
 */
public class ODResourceAcquiredMessage extends StandartMessage {
  /** ���������� ��� ���������. */
  public static final String NAME = "od_resource_acquired";

  /** ��� �������. */
  private transient String resourceName = "";
  /** ������ ����� �������. */
  private static final String RESNAME_IDX = "0";
  /** ������. */
  private transient Resource resource;
  /** ������ �������. */
  private static final String RES_IDX = "1";

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
      return (Resource) getField(RES_IDX);
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
      getContents().clear();
      addField(RESNAME_IDX, resourceName);
      addField(RES_IDX, resource);
      setCE(true);
    }
    return isCE();
  }
  public final boolean isRoutable() {
    return false;
  }
} // ODResourceAcquiredMessage
