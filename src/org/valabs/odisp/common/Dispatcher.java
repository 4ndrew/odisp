package com.novel.odisp.common;

import java.util.List;

/** ����������� ��������� ����������, ������� ������ �������������
 * �����, ������� ��������� � �������� ODISP ����������.
 * @author (C) 2003 <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
 * @version $Id: Dispatcher.java,v 1.13 2004/08/18 12:48:39 valeks Exp $
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
  /** ������ � ����������� ����������. */
  ExceptionHandler getExceptionHandler();
  /** ��������� ������ ����������� ����������. */
  void addExceptionHandler(ExceptionHandler ex);
}
