package com.novel.stdmsg;

import com.novel.odisp.*;
import com.novel.odisp.common.*;

/** ����� ����������� ��������� ���������� ODISP �� ��������.
 * ��� ��������� ������ ������������� ������ �����������.
 * � �������� ��������� ����� ������� ��� ������ ������� ����� ���������� ��������.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: ODShutdownMessage.java,v 1.1 2003/12/01 22:19:29 valeks Exp $
 */

public class ODShutdownMessage extends StandartMessage {
  /** ������� ����� ��������� � ��������� ����������� 
   * @param origin �����������
   * @param destination ����������
   * @param replyTo ������ ��������� �� ������� ������������ �����
   */
  public ODShutdownMessage(String origin, String destination, int replyTo) {
    super("od_shutdown", origin, destination, replyTo);
  }

  /** ������� �������� ���� ������
   * @return ��� ������
   */
  public int getExitCode() {
    int exitCode = 0;
    if (getFieldsCount() == 1) {
      exitCode = ((Integer) getField(0)).intValue();
    }
    return exitCode;
  }

  /** ���������� �������� ���� ������
   * @param newExitCode ����� �������� ���� ������
   */
  public void setExitCode(int newExitCode) {
    fields.removeAllItems();
    addField(new Integer(newExitCode));
  }
  
}// ODShutdown
