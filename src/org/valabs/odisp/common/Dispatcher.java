package org.valabs.odisp.common;

import java.util.List;

import org.doomdark.uuid.UUID;

/** ����������� ��������� ����������, ������� ������ �������������
 * �����, ������� ��������� � �������� ODISP ����������.
 * @author (C) 2003 <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
 * @version $Id: Dispatcher.java,v 1.15 2004/11/05 14:11:29 valeks Exp $
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
			String origin, UUID inReplyTo);
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
