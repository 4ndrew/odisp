package com.novel.stdmsg;

import com.novel.odisp.common.Message;
import com.novel.odisp.common.Resource;

/** ������ ���������� �� ������������� �������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODReleaseMessage.java,v 1.12 2004/07/16 12:11:27 boris Exp $
 */

public class ODReleaseMessage {
  /** ���������� ��� ���������. */
  public static final String NAME = "od_release";
  /** ������ ����� � ���������. */
  private static final String NAME_IDX = "resname";
  /** ������ ������� � ���������. */
  private static final String RES_IDX = "resobj";

  /** ������������� ��������� ���������� � �������� �� ������������� �������.
   * @param msg ���������
   * @param origin �����
   * @param replyTo � ����� �� ��������� No.
   */
  public static final void setup(final Message msg, final String origin, final int replyTo) {
    msg.setAction(NAME);
    msg.setDestination("dispatcher");
    msg.setReplyTo(replyTo);
    msg.setOrigin(origin);
    msg.setRoutable(false);
    msg.setCorrect(false);
  }

  /** ������� ��� ������� ��� �������.
   * @param msg ���������
   * @return ��� �������
   */
  public static final String getResourceName(final Message msg) {
    return (String) msg.getField(NAME_IDX);
  }

  /** ���������� ��� �������.
   * @param msg ���������
   * @param newName ����� ���
   * @return ������ �� ������� ���������
   */
  public static final void setResourceName(final Message msg, final String newName) {
    msg.addField(NAME_IDX, newName);
    if (msg.getContents().size() == 2) {
      msg.setCorrect(true);
    }
  }

  /** ������� ������ �� ������.
   * @param msg ���������
   * @return ������ �� ������
   */
  public static final Resource getResource(final Message msg) {
    return (Resource) msg.getField(RES_IDX);
  }

  /** ���������� ����� �������� ������ �� ������.
   * @param msg ���������
   * @param newResource ����� �������� ������
   * @return ������ �� ������� ���������
   */
  public static final void setResource(final Message msg, final Resource newResource) {
    msg.addField(RES_IDX, newResource);
    if (msg.getContents().size() == 2) {
      msg.setCorrect(true);
    }
  }
  public static final boolean equals(final Message msg) {
    return msg.getAction().equals(NAME);
  }
} // ODReleaseMessage
