package org.valabs.odisp.common;

import java.util.List;

import org.doomdark.uuid.UUID;

/** ����������� ��������� ����������, ������� ������ �������������
 * �����, ������� ��������� � �������� ODISP ����������.
 * @author (C) 2003 <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
 * @version $Id: Dispatcher.java,v 1.18 2005/07/22 15:32:02 valeks Exp $
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

  /** ������ � ��������� ��������. 
   * @return ������ �� �������� ��������
   */
  ObjectManager getObjectManager();
  /** ������ � ��������� ��������. 
   * @return ������ �� �������� ��������
   */
  ResourceManager getResourceManager();
  /** ������ � ��������� ������������.
   * @return ������ �� �������� ������������
   */
  SecurityManager getSecurityManager();
  /** ������ � ��������� ������������.
   * @return ������ �� �������� ������������
   */
  ConfigurationManager getConfigurationManager();
  /** ������ � ����������� ����������. 
   * @return ������ �� ���������� ����������
   */
  ExceptionHandler getExceptionHandler();
  /** ���������� ��������� ������������. 
   * @param additionalSecurityManager ����� �������� ������������
   */
  void addSecurityManager(SecurityManager additionalSecurityManager);
  /** ��������� ������ ����������� ����������.
   * @param exception ����� ���������� ����������
   */
  void addExceptionHandler(ExceptionHandler exception);
  /** ���������� ��������� ������������. 
   * @param cman ����� �������� ������������
   */
  void addConfigurationManager(ConfigurationManager cman);
}
