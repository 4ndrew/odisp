package com.novel.odisp.common;

import java.util.List;
import java.util.Map;

/** ��������� ��������� �������� ODISP.
 * @author (C) 2004 <a href="mailto:valeks@valeks.novel.local">Valentin A. Alekseev</a>
 * @version $Id: ResourceManager.java,v 1.5 2004/02/23 14:47:21 valeks Exp $
 */

public interface ResourceManager {
  /** �������� �������.
   * @param className ��� ������
   * @param mult ���������
   */
  void loadResource(String className, int mult);
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
