package com.novel.odisp.common;

import java.util.Map;

/** ��������� ��������� �������� ODISP.
 * @author (C) 2004 <a href="mailto:valeks@valeks.novel.local">Valentin A. Alekseev</a>
 * @version $Id: ObjectManager.java,v 1.1 2004/02/13 12:11:43 valeks Exp $
 */

public interface ObjectManager {
  /** ������ � ������ ��������. */
  Map getObjects();
  /** �������� �������. */
  void loadObject(String name);
  /** �������� �������. */
  void unloadObject(String objectName, int code);
  /** ��������� ��������������� ��������. */
  void loadPending();
  /** ������� ���������. */
  void send(Message m);
  /** ��������� ��������� ���������� �������. */
  int getBlockedState(String objName);
  /** ��������� ��������� ���������� �������. */
  void setBlockedState(String objName, int newState);
}// ObjectManager
