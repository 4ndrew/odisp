/* ODISP -- Message Oriented Middleware
 * Copyright (C) 2003-2005 Valentin A. Alekseev
 * Copyright (C) 2003-2005 Andrew A. Porohin 
 * 
 * ODISP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.1 of the License.
 * 
 * ODISP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ODISP.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.valabs.stdmsg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.doomdark.uuid.UUID;
import org.doomdark.uuid.UUIDGenerator;
import org.valabs.odisp.common.Message;

/** ���������� ������������ ��������� ��� ������������ ���������� ODISP.
 * 
 * @author (C) 2003-2005 <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
 * @author (C) 2003-2005 <a href="mailto:dron@novel-il.ru">������ �. �������</a>
 * @version $Id: StandartMessage.java,v 1.35 2005/11/21 15:02:54 dron Exp $
 */
public class StandartMessage implements Message, Serializable, Cloneable {
  static final long serialVersionUID = 1L;
  /** ���� �������������. */
  private boolean routable = true;
  /** ���������� ������ ��������� � �������. */
  private UUID myId;
  /** ������ ����� ���������. */
  private final Map fields = new Hashtable(5);
  /** ��������. */
  private String action = null;
  /** ����� ����������. */
  private String destination = null;
  /** �����������. */
  private String origin = null;
  /** ������������� ��������� �� ������� ������������ �����. */
  private UUID inReplyTo = null;
  /** ���� ���������� ��������. */
  private boolean ce = false;
  /** ������ ������������. */
  private List envelope = null;
  /** ������� OOB. */
  private boolean oob = false;
  /** ���������� ������������ ���������.
   * @param newAction �������� ������� ����� ���������
   * @param newDestination ������� ���������
   * @param newOrigin ����������� ���������
   * @param newInReplyTo ������������� ��������� �� ������� ������������ �����
   */
  public StandartMessage(final String newAction,
			 final String newDestination,
			 final String newOrigin,
			 final UUID newInReplyTo) {
    action = newAction;
    destination = newDestination;
    inReplyTo = newInReplyTo;
    origin = newOrigin;
    myId = UUIDGenerator.getInstance().generateTimeBasedUUID();
  }

  /** ����������� ��-���������. */
  public StandartMessage() {
    myId = UUIDGenerator.getInstance().generateTimeBasedUUID();
  }

  /** ����������� ���������. */
  private void copyFrom(final Message msg, final boolean noKeepId) {
    action = msg.getAction();
    destination = msg.getDestination();
    inReplyTo = msg.getReplyTo();
    origin = msg.getOrigin();
    fields.clear();
    fields.putAll(msg.getContents());
    routable = msg.isRoutable();
    if (noKeepId) {
      myId = UUIDGenerator.getInstance().generateTimeBasedUUID();
    } else {
      myId = msg.getId();
    }
    setCorrect(msg.isCorrect());
  }

  /** ���������� �����������.
   * @param msg ��������� � �������� ��������� �����
   * @param noKeepId �� ��������������� ������������� ���������
   */
  public StandartMessage(final Message msg, final boolean noKeepId) {
    copyFrom(msg, noKeepId);
  }

  /** ���������� ����������� ���������� ����� ���������. 
   * @param msg �������� ���������
   */
  public StandartMessage(final Message msg) {
    copyFrom(msg, false);
  }

  /** ���������� �������� ������� ����� ���������.
   * @return ��������
   */
  public final String getAction() {
  	assert action != null : "Action is null";
    return action;
  }

  /** ������������� �������� ������� ����� ���������.
   * @param newAction ��������
   */
  public final void setAction(final String newAction) {
    action = newAction;
  }

  /** ���������� �������� ���������.
   * @return �������
   */
  public final String getDestination() {
  	assert destination != null : "Destination is null";
  	return destination;
  }

  /** ������������� �������� ���������.
   * @param newDest �������
   */
  public final void setDestination(final String newDest) {
    destination = newDest;
  }

  /** ���������� ������ ���������.
   * @return �����
   */
  public final String getOrigin() {
  	assert origin != null : "Origin is null";
    return origin;
  }

  /** �������� ����������� �� ����.
   * @param newOrigin ����� �������� ���� �����������
   */
  public final void setOrigin(final String newOrigin) {
    this.origin = newOrigin;
  }

  /** ���������� ������������� ��������� �� ������� ������������ �����.
   * @return �������������
   */
  public final UUID getReplyTo() {
    return inReplyTo;
  }

  /** ������������� ������������� ��������� �� ������� ������������ �����.
   * @param newId �������������
   */
  public final void setReplyTo(final UUID newId) {
    inReplyTo = newId;
  }

  /** ���������� ���������� ����� � ���������.
   * @return ���-�� �����
   */
  public final int getFieldsCount() {
    return fields.size();
  }

  /** ���������� ���������� ������������� ���������.
   * @return �������������
   */
  public final UUID getId() {
    return myId;
  }

  /** ������������� ���������� ������������� ���������.
   * @param newId �������������
   */
  public final void setId(final UUID newId) {
    myId = newId;
  }

  /** ������������� ��������� � ���� ��������� ������.
   * @return �������� ������������� ���������
   */
  public final String toString() {
    return "stdmessage id=" + myId + " replyto=" + inReplyTo
      + " action=" + action + ", destination=" + destination
      + ", origin=" + origin + ", fields.size()=" + fields.size() + (isRoutable() ? " routable" :"");
  }

  /** ������������� ��������� � ���� ��������� ������ � ������ ���� ��������� ������.
   * @return �������� ������������� ���������
   * @param doStackTrace �������� ��� ��� ���� ������� �������
   */
  public final String toString(final boolean doStackTrace) {
    String result = toString();
    if (doStackTrace) {
      // ��������� ��� ��� ����, ��� �� �������� ������ ������� �������
      StackTraceElement[] stea = {};
      try {
        throw new IllegalArgumentException();
      } catch (Exception e) {
        stea = e.getStackTrace();
      }
      result += "\nStack trace:";
      for (int i = 1; i < 10 && i < stea.length; i++) {
        result += stea[i] + "\n";
      }
      String fieldDump = "\nField dump:";
      final Iterator it = fields.keySet().iterator();
      while (it.hasNext()) {
        final String key = (String) it.next();
        fieldDump += "\n(" + key + ") " + getField(key);
      }
      result += fieldDump;
    }
    return result;
  }

  /** �������� ������������ ���������.
   * @return ���� ������������
   */
  public boolean isCorrect() {
    return ce;
  }

  public final Map getContents() {
    return fields;
  }

  /** ��������� ����� ������������.
   * @param newCE true -- ��������� ���������, false -- ��������� �����������
   */
  public final void setCorrect(final boolean newCE) {
    ce = newCE;
  }

  public boolean isRoutable() {
    return routable;
  }
  public void setRoutable(final boolean newRoutable) {
    routable = newRoutable;
  }

  public void addField(final String name, final Object value) {
    if (value == null) {
      return;
    }
    if (fields.containsKey(name)) {
      fields.remove(name);
    }
    fields.put(name, value);
  }

  public Object getField(final String name) {
    return fields.get(name);
  }
  
  public final List getEnvelope() {
  	return envelope;
  }
  
  public final void addToEnvelope(final Message envelopeMessage) {
      if (envelope == null) {
        envelope = new ArrayList();
      }
      envelope.add(envelopeMessage);
  }
  
  /** �������� �� OOB.
   * @return true ���� ��������� OOB.
   */
  public boolean isOOB() {
//    debugMGW.logMessage(this);
    return oob;
  }
  
  /** ��������� ����� OOB.
   * @param newValue ����� ��������.
   */
  public void setOOB(boolean newValue) {
    oob = newValue;
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Message#cloneMessage()
   */
  public final Message cloneMessage() {
    Message result = null;
    try {
      result = (Message) this.clone();
    } catch (CloneNotSupportedException e) { /* ������������ */ }
    return result;
  }
}
