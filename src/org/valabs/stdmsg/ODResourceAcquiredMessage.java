package com.novel.stdmsg;

import com.novel.odisp.common.Resource;
import com.novel.odisp.common.Message;

/** ����� �� ������ � ������� �������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODResourceAcquiredMessage.java,v 1.12 2004/06/09 19:44:43 valeks Exp $
 */
public class ODResourceAcquiredMessage extends StandartMessage {
  /** ���������� ��� ���������. */
  public static final String NAME = "od_resource_acquired";

  /** ��� �������. */
  private transient String resourceName = "";
  /** ������ ����� �������. */
  private static final String RESNAME_IDX = "resname";
  /** ������. */
  private transient Resource resource;
  /** ������ �������. */
  private static final String RES_IDX = "resobj";

  /** ����������� ���������.
   * @param destination ���������� ���������
   * @param replyId ��������� �� ������� ������������ �����
   */
  public static final void setup(final Message msg,
				 final String destination,
				 final int replyId) {
    msg.setAction(NAME);
    msg.setOrigin("dispatcher");
    msg.setDestination(destination);
    msg.setReplyTo(replyId);
    msg.setRoutable(false);
    msg.setCorrect(false);
  }
  /** ������� ��� ������ �������.
   * @return ��� ������
   */
  public static final String getResourceName(final Message msg) {
    return (String) msg.getField(RESNAME_IDX);
  }

  /** ���������� ��� ������ �������.
   * @param newResourceName ����� ��� ������
   * @return ������ �� ���������
   */
  public final static void setResourceName(final Message msg, final String newResourceName) {
    msg.addField(RESNAME_IDX, newResourceName);
    if (msg.getContents().size() == 2) {
      msg.setCorrect(true);
    }
  }

  /** ������� ������ �� ������.
   * @return ������ �� ������
   */
  public final static Resource getResource(final Message msg) {
    return (Resource) msg.getField(RES_IDX);
  }

  /** ���������� ������ �� ������.
   * @param newResource ����� �������� ������
   * @return ������ �� ���������
   */
  public final static void setResource(final Message msg, final Resource newResource) {
    msg.addField(RES_IDX, newResource);
    if (msg.getContents().size() == 2) {
      msg.setCorrect(true);
    }
  }

  public static final boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
} // ODResourceAcquiredMessage
