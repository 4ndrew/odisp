package com.novel.odisp.common;

import java.util.Map;

/** ��������� ��������� �������� ODISP.
 * @author (C) 2004 <a href="mailto:valeks@valeks.novel.local">Valentin A. Alekseev</a>
 * @version $Id: ResourceManager.java,v 1.3 2004/02/13 15:16:03 valeks Exp $
 */

public interface ResourceManager {
  /** �������� �������.
   * @param className ��� ������
   * @param mult ���������
   * @param param �������� �������
   */
  void loadResource(String className, int mult, String param);
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
} // ResourceManager
