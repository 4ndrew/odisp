package com.novel.odisp.common;

import java.util.Map;
import com.novel.odisp.common.Resource;

/** ��������� ��������� �������� ODISP.
 * @author (C) 2004 <a href="mailto:valeks@valeks.novel.local">Valentin A. Alekseev</a>
 * @version $Id: ResourceManager.java,v 1.2 2004/02/13 13:15:17 valeks Exp $
 */

public interface ResourceManager {
  /** �������� �������.
   * @param className ��� ������
   * @param mult ���������
   * @param param �������� �������
   */
  public void loadResource(String className, int mult, String param);
  /** �������� �������.
   * @param name ��� ������
   * @param code ��� ������
   */
  public void unloadResource(String name, int code);
  /** �������� ������ ��������.
   * @return ����� ��������
   */
  public Map getResources();
  /** ������ �� ������ �������.
   * @param msg ��������� � �������
   */
  public void acquireRequest(Message msg);
  /** ������ �� ������������� �������.
   * @param msg ��������� � �������
   */
  public void releaseRequest(Message msg);
}// ResourceManager
