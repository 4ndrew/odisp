package org.valabs.stdmsg;

import org.doomdark.uuid.UUID;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.Resource;

/** ����� �� ������ � ������� �������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODResourceAcquiredMessage.java,v 1.17 2004/11/05 14:11:28 valeks Exp $
 */
public class ODResourceAcquiredMessage {
  /** ���������� ��� ���������. */
  public static final String NAME = "od_resource_acquired";
  /** ������ ����� �������. */
  private static final String RESNAME_IDX = "resname";
  /** ������ �������. */
  private static final String RES_IDX = "resobj";

  /** ����������� ���������.
   * @param destination ���������� ���������
   * @param replyId ��������� �� ������� ������������ �����
   */
  public static final void setup(final Message msg,
				 final String destination,
				 final UUID replyId) {
    msg.setAction(NAME);
    msg.setOrigin("dispatcher");
    msg.setDestination(destination);
    msg.setReplyTo(replyId);
    msg.setRoutable(false);
    msg.setCorrect(false);
    msg.setOOB(true);
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
