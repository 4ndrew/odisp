package com.novel.odisp.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/** ����������� ������ ODISP.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: StandartODObject.java,v 1.1 2004/05/13 09:22:38 valeks Exp $
 */

public abstract class StandartODObject implements ODObject {
  /** ������. */
  protected Logger logger;
  /** ��������� ���������� � ���� ��������. */
  protected Dispatcher dispatcher;
  /** ������ ��������� � ���������. */
  private List messages;
  /** ������� ���������������� ����������. */
  private Map configuration;
  /** Regex ����� ����������� ���������.
   * �� ��������� ���������������� ������ �������. */
  private String match;
  /** ���������� ��� ������� � ���� ODISP. */
  private String name;
  /** ����� ������������ ���������. */
  private Map handlers;
  /** ������ ��������� ��������� �� ����������� ������������. */
  private List unhandledMessages = new LinkedList();
  /** �������� ����� ����������� ���������.
   * @param newMatch ����� �����
   */
  protected final void setMatch(final String newMatch) {
    match = newMatch;
  }

  /** ����������� ���������������� �������� ����.
   * @param newName ��� �������
   */
  public StandartODObject(final String newName) {
    messages = new ArrayList();
    name = newName;
    match = newName;
    logger = Logger.getLogger(newName);
    logger.setLevel(java.util.logging.Level.ALL);
    handlers = new HashMap();
    registerHandlers();
  }

  /** ������ � ����������.
   * @return ������ �� ���������
   */
  protected final Dispatcher getDispatcher() {
    return dispatcher;
  }

  /** ���������� ���������� ODISP ��� �������.
   * @return ODISP ��� �������
   */
  public final String getObjectName() {
    return name;
  }

  /** ������������� ���������� ��� �������� �������.
   * @param d ��������� ���������� � ���� ��������
   */
  public final void setDispatcher(final Dispatcher d) {
    this.dispatcher = d;
  }

  /** ���������� ������� ����������.
   * @param cfg ����� �������
   */
  public final void setConfiguration(final Map cfg) {
    configuration = cfg;
  }
  /** �������� �������� ��������� ������������.
   * @param name ��� ���������
   */
  protected final String getParameter(final String name) {
    if (configuration != null && configuration.containsKey(name)) {
      return (String) configuration.get(name);
    }
    return null;
  }
  /** �������� �������� ��������� ������������ � ������ �������� ��-���������.
   * @param name ��� ���������
   * @param defValue �������� �� ���������
   */
  protected final String getParameter(final String name, final String defValue) {
    return getParameter(name) == null ? defValue : getParameter(name);
  }

  /** ����������� ����������� ���������.
   * @param message ���������� ��� ���������
   * @param handler ����������
   */
  protected final void addHandler(final String message,
				  final MessageHandler handler) {
    if (handlers.containsKey(message)) {
      return;
    }
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
  protected void handleMessage(final Message msg) {
    if (handlers.containsKey(msg.getAction())) {
      ((MessageHandler) handlers.get(msg.getAction())).messageReceived(msg);
    } else {
      logger.finer(" (" + getObjectName() + ") there is no handler for message " + msg.getAction());
    }
  }

  /** ����������� ������ ������ ����������� ���� �����
   * ��� ����������� ������������. */
  protected void registerHandlers() {
    /* DO NOTHING BY DEFAULT */
  }

  /** ����� ���������� ��� ������� ������ ������.
   * @param type ������� ������
   * @return ��� ��������
   */
  public int cleanUp(final int type) {
    return 0;
  }

} // StandartODObject