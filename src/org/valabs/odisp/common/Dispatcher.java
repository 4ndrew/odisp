package com.novel.odisp.common;

import java.util.List;

/** ����������� ��������� ����������, ������� ������ �������������
 * �����, ������� ��������� � �������� ODISP ����������.
 * @author �������� �. ��������
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: Dispatcher.java,v 1.11 2004/07/01 13:45:39 valeks Exp $
 */
public interface Dispatcher {
  /** ������� ���������� ���������.
   * @param message ���������
   */
  void send(Message message);
  /** ������� ������ ���������.
   * @param messageList ������ ���������
   */
  void send(Message[] messageList);
  /** ������� ������ ���������.
   * @param messageList ������ ���������
   */
  void send(List messageList);


  /** ��������� ������ ��������� � ��������� �����������.
   * @param action ��������
   * @param destination ����������
   * @param origin �����������
   * @param inReplyTo ��������� �� ������� ������������ �����
   * @return ���������
   */
  Message getNewMessage(String action, String destination,
			String origin, int inReplyTo);
  /** ������� ������ ���������.
   * @return ������ �� ���������
   */
  Message getNewMessage();

  /** ������ � ��������� ��������. */
  ObjectManager getObjectManager();
  /** ������ � ��������� ��������. */
  ResourceManager getResourceManager();
  /** ������ � ��������� ������������. */
  SecurityManager getSecurityManager();
  /** ���������� ��������� ������������. */
  void addSecurityManager(SecurityManager additionalSecurityManager);
}
