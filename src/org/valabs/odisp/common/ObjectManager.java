package com.novel.odisp.common;

import java.util.Map;

/** ��������� ��������� �������� ODISP.
 * @author (C) 2004 <a href="mailto:valeks@valeks.novel.local">Valentin A. Alekseev</a>
 * @version $Id: ObjectManager.java,v 1.2 2004/02/13 15:16:03 valeks Exp $
 */

public interface ObjectManager {
  /** ������ � ������ ��������.
   * @return ������ ��������
   */
  Map getObjects();
  /** �������� �������.
   * @param name ��� ������
   */
  void loadObject(String name);
  /** �������� �������.
   * @param objectName ��� �������
   * @param code ��� ������
   */
  void unloadObject(String objectName, int code);
  /** ��������� ��������������� ��������. */
  void loadPending();
  /** ������� ���������.
   * @param m ���������
   */
  void send(Message m);
  /** ��������� ��������� ���������� �������.
   * @param objName ��� �������
   * @return ��������� ����������
   */
  int getBlockedState(String objName);
  /** ��������� ��������� ���������� �������.
   * @param objName ��� �������
   * @param newState ����� ���������
   */
  void setBlockedState(String objName, int newState);
} // ObjectManager
