package com.novel.odisp.common;

import java.util.List;
import java.util.Map;

/** ��������� ��������� �������� ODISP.
 * @author (C) 2004 <a href="mailto:valeks@valeks.novel.local">Valentin A. Alekseev</a>
 * @version $Id: ObjectManager.java,v 1.6 2004/05/21 21:49:28 valeks Exp $
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
