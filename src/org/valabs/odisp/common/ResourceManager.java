package com.novel.odisp.common;

import java.util.List;
import java.util.Map;

/** ��������� ��������� �������� ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ResourceManager.java,v 1.7 2004/08/18 12:48:40 valeks Exp $
 */

public interface ResourceManager {
  /** �������� �������.
   * @param className ��� ������
   * @param mult ���������
   */
  void loadResource(String className, int mult, Map params);
  /** �������� �������.
   * @param name ��� ������
   * @param code ��� ������
   */
  void unloadResource(String name, int code);
  /** �������� ������ ��������.
   * @return ����� ��������
   */
  Map getResources();
  /** ������ �� ������ �������.
   * @param msg ��������� � �������
   */
  void acquireRequest(Message msg);
  /** ������ �� ������������� �������.
   * @param msg ��������� � �������
   */
  void releaseRequest(Message msg);
  /** ������� ���������� �� ��������.
   * @return ������ �����.
   */
  List statRequest();
} // ResourceManager
