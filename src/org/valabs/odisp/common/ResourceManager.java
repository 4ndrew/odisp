package org.valabs.odisp.common;

import java.util.List;
import java.util.Map;

/**
 * ��������� ��������� �������� ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev
 *         </a>
 * @version $Id: ResourceManager.java,v 1.10 2004/11/28 09:37:21 valeks Exp $
 */

public interface ResourceManager {
  /**
   * �������� �������.
   * @param className ��� ������
   * @param mult ���������
   */
  void loadResource(String className, int mult, Map params);

  /**
   * �������� �������.
   * @param name ��� ������
   * @param code ��� ������
   */
  void unloadResource(String name, int code);

  /**
   * �������� ������ ��������.
   * @return ����� ��������
   */
  Map getResources();

  /**
   * ������� ���������� �� ��������.
   * @return ������ �����.
   */
  List statRequest();

  /**
   * ����������� ������� ������� ������� ��������. <b>��������! </b> ��� ���,
   * ������, ������� ������������ � ��������� ������������ ��������� (�,
   * ��������������, � ����� Sender), ����������� ����� ������ ��������������
   * ����������� ��������� ����� �������� �������� ������ ���������� ������� �
   * ������ ������������� ������������ ��������. ��� ����, ��� �� ����������
   * �������� ���� �������, � ��� �� �� �������� ������� ����� Sender,
   * ���������� ���� ������������ ������������� �������, ���� �������������
   * ������ ������� � ��������� ����, �������� ���: 
   * <pre>
   * ...
   * SomeResource res = null;
   * new Thread() {
   *   public void run() {
   * 		setBlockedState(true);
   * 		res = dispatcher.resourceAcquire(SomeResource.class.getName());
   * 		setBlockedState(false);
   * 	}
   * }.start();
   * ...
   * </pre>
   * ����� ������� ���������� ������ ������� ������������ � ��������� ������ ���
   * ��������������� ��������� �������.
   * @param className ��� �������
   * @return ������ �� ������
   */
  Resource resourceAcquire(String className);

  /**
   * ������������� ������� ������� ������� ��������.
   * @param className ��� �������
   * @return ������ �� ������ ��� null ���� ������ ������������
   */
  Resource resourceTryAcquire(String className);

  /**
   * ������������� �������.
   * @param className ��� �������
   * @param resource ������ �� ������
   */
  void releaseResource(String className, Resource resource);
} // ResourceManager
