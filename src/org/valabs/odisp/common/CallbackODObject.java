package com.novel.odisp.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Pattern;

/** ������� ����� ����������� �������� ���� ��� ���������
* ���������� ����������� ODISP � ���������� ���������� ���������
* �� ���� �������.
* @author �������� �. ��������
* @author (�) 2003, ��� "�����-��"
* @version $Id: CallbackODObject.java,v 1.14 2004/05/11 09:55:39 valeks Exp $
*/
public abstract class CallbackODObject extends ODObject {
  /** ����� ������������ ���������. */
  private Map handlers;
  /** ������� ������ ������ ����������� ������������. */
  private boolean handlersRegistred = false;
  /** ������ ��������� ��������� �� ����������� ������������. */
  private List unhandledMessages = new LinkedList();
  /** ����������� ������� � �������� ������.
   * @param name ��� �������
   */
  public CallbackODObject(final String name) {
    super(name);
    handlers = new HashMap();
  }
  /** ���������� ������ ����������� �������.
      @param message ��������� �������������� ������������
      @param handler �����-���������� ���������
  */
  protected final void addHandler(final String message,
				  final MessageHandler handler) {
    if (handlers.containsKey(message)) {
      return;
    }
    logger.finest("registered handler for " + message);
    handlers.put(message, handler);
  }

  /** ��������� ���������� ��������� � ����.
   * @param msg ���������
   */
  public final void addMessage(final Message msg) {
    if (!Pattern.matches(match, msg.getDestination())
	&& !Pattern.matches(msg.getDestination(), getObjectName())) {
      return;
    }
    handleMessage(msg);
  }
  /** ��������� ���������.
   * @param msg ��������� ��� ���������
   */
  protected final void handleMessage(final Message msg) {
    if (!handlersRegistred) {
      registerHandlers();
    }
    if (handlers.containsKey(msg.getAction())) {
      ((MessageHandler) handlers.get(msg.getAction())).messageReceived(msg);
    } else {
      logger.finer("there is no handler for message " + msg.getAction());
    }
  }
  /** ����� ���������� ��� ������� ������ ������.
   * @param type ������� ������
   * @return ��� ��������
  */
  public int cleanUp(final int type) {
    return 0;
  }
  /** ����������� ������ ������ ������������� ���� �����
   * ��� ����������� ������������. */
  protected abstract void registerHandlers();
}
