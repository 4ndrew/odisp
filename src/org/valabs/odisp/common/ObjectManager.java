package com.novel.odisp.common;

import java.util.Map;
import java.util.List;

/** ��������� ��������� �������� ODISP.
 * @author (C) 2004 <a href="mailto:valeks@valeks.novel.local">Valentin A. Alekseev</a>
 * @version $Id: ObjectManager.java,v 1.4 2004/03/27 19:40:18 valeks Exp $
 */

public interface ObjectManager {
  /** ������ � ������ ��������.
   * @return ������ ��������
   */
  Map getObjects();
  /** �������� �������.
   * @param name ��� ������
   * @param params ��������� ��������
   */
  void loadObject(String name, Map params);
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
  /** �������� ������ ��������.
   * @return ���������������� ������ ��������
   */
  List getProviding();
  /** ������� ���������� �� ������.
   * @param service �������� �������
   * @param objectName �������� �������
   */
  void removeProvider(String service, String objectName);
  /** �������� ���������� � ������.
   * @param service �������� �������
   * @param objectName �������� �������
   */
  void addProvider(String service, String objectName);
} // ObjectManager
