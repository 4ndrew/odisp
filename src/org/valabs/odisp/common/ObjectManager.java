package org.valabs.odisp.common;

import java.util.List;
import java.util.Map;

/** ��������� ��������� �������� ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ObjectManager.java,v 1.10 2005/03/03 08:47:50 valeks Exp $
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
   * @param message ���������
   */
  void send(Message message);
  /** �������� ������ ��������.
   * @return ���������������� ������ ��������
   */
  List getProviding();
  /** ������� ���������� �� ������.
   * @param service �������� �������
   * @param objectName �������� �������
   * @deprecated ������� � ������ 1.17 StandartODObject'� ��� �����������
   * ����������� �������� ������ �������� �������, ������� �������� ��� ��������
   * ���������� destination ��������� � �����/�������� ��������������� ��������.
   */
  void removeProvider(String service, String objectName);
  /** �������� ���������� � ������.
   * @param service �������� �������
   * @param objectName �������� �������
   * @deprecated ������� � ������ 1.17 StandartODObject'� ��� �����������
   * ����������� �������� ������ �������� �������, ������� �������� ��� ��������
   * ���������� destination ��������� � �����/�������� ��������������� ��������.
   */
  void addProvider(String service, String objectName);
} // ObjectManager
