package org.valabs.stdmsg;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.doomdark.uuid.UUID;
import org.doomdark.uuid.UUIDGenerator;
import org.valabs.odisp.common.Message;

/** ���������� ������������ ��������� ��� ������������ ���������� ODISP.
 * 
 * @author (C) 2003-2004 <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
 * @author (C) 2003-2004 <a href="mailto:dron@novel-il.ru">������ �. �������</a>
 * @version $Id: StandartMessage.java,v 1.26 2005/02/27 12:37:32 valeks Exp $
 */
public class StandartMessage implements Message, Serializable, Cloneable {
  private static MessageGraphWriter debugMGW = new MessageGraphWriter();
  /** ���� �������������. */
  private boolean routable = true;
  /** ���������� ������ ��������� � �������. */
  private UUID myId;
  /** ������ ����� ���������. */
  private final Map fields = new HashMap();
  /** ��������. */
  private String action = null;
  /** ����� ����������. */
  private String destination = null;
  /** �����������. */
  private String origin = null;
  /** ������������� ��������� �� ������� ������������ �����. */
  private UUID inReplyTo = null;
  /** ������ ���������� ������������ ����. */
  private int lastIdx = 0;
  /** ���� ���������� ��������. */
  private boolean ce = false;
  /** ������ ������������. */
  private final List envelope = new ArrayList();
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

  /** ���������� ����������� ���������� ����� ���������. */
  public StandartMessage(final Message msg) {
    copyFrom(msg, false);
  }

  /** ���������� ������������� ������� � ���� ���������.
   * @param field ������ ������� ����� �������� ���������
   * @deprecated ����������� addField(String, Object)
   */
  protected final void addField(final Object field) {
    addField((new Integer(lastIdx++)).toString(), field);
  }

  /** ������� ������������ � ���� ��������� ������� �� �������.
   * @param field ������ �������
   * @return ���� ���������
   * @deprecated ����������� getField(String)
   */
  protected final Object getField(final int field) {
    return getField((new Integer(field)).toString());
  }

  /** ���������� �������� ������� ����� ���������.
   * @return ��������
   */
  public final String getAction() {
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
  public final String getDestination() { return destination; }

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

  /** ������ �� ����� ������ �����.
   * @return ������ �����
   * @deprecated ����������� getContents()
   */
  protected final List getFields() {
    return new ArrayList(fields.values());
  }

  public final Map getContents() {
    return fields;
  }

  /** ��������� ����� ������������.
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
  		envelope.add(envelopeMessage);
  }
  
  /** �������� �� OOB.
   * @return true ���� ��������� OOB.
   */
  public boolean isOOB() {
    debugMGW.logMessage(this);
    return oob;
  }
  
  /** ��������� ����� OOB.
   * @param newValue ����� ��������.
   */
  public void setOOB(boolean newValue) {
    oob = newValue;
  }
 
  /** ��������� ����� ��������� � ������� DOT. */
  static class MessageGraphWriter {
    private final MessageGraphWriter_MSGS msgs = new MessageGraphWriter_MSGS("msgs.dot");
    private final MessageGraphWriter_OBJECTS objects = new MessageGraphWriter_OBJECTS("objects.dot");
    
    public void logMessage(final Message msg) {
      msgs.logMessage(msg);
      objects.logMessage(msg);
    }
  
    /** ���� ��������� ���������. */
    class MessageGraphWriter_OBJECTS {
      private final Set objects = new HashSet();
      private final List messages = new ArrayList();

      public MessageGraphWriter_OBJECTS(final String outfName) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
          public final void run() {
            PrintStream output;
            try {
              output = new PrintStream(new FileOutputStream(outfName));
            } catch (IOException e) {
              output = new PrintStream(System.err);
            }
            output.println("digraph messagesobjects {");
            Iterator it = objects.iterator();
            final String namePrefix = " \"n";
            while (it.hasNext()) {
              String element = (String) it.next();
              output.println(namePrefix + element + "\" [label=\"" + element + "\"];");
            }
            it = messages.iterator();
            while (it.hasNext()) {
              final mrec element = (mrec) it.next();
              output.println(namePrefix + element.origin + "\" -> " + namePrefix + element.destination + "\" [label=\""
                      + element.action + "\"];");
            }
            output.println("}");
            output.close();
          }
        });
      }

      public void logMessage(final Message msg) {
        objects.add(msg.getOrigin());
        objects.add(msg.getDestination());
        final mrec rec = new mrec();
        rec.origin = msg.getOrigin();
        rec.destination = msg.getDestination();
        rec.action = msg.getAction();
        rec.id = msg.getId();
        rec.replyid = msg.getReplyTo();
        messages.add(rec);
      }
      class mrec {
        String origin;
        String destination;
        String action;
        UUID id;
        UUID replyid;
      }
    }
    
    /** ���� ������������������ ���������. */
    static class MessageGraphWriter_MSGS {

      private PrintStream output;

      public MessageGraphWriter_MSGS(String outfName) {
        try {
          output = new PrintStream(new FileOutputStream(outfName));
        } catch (IOException e) {
          output = new PrintStream(System.err);
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
          public void run() {
            output.println("}");
            output.close();
          }
        });
        output.println("digraph messages {");
        output.println("  \"n" + UUID.getNullUUID() + "\" [label=\"No origin\"];");
      }

      public void logMessage(final Message msg) {
        output.println("  \"n" + msg.getId() + "\" [label=\"" + msg.getAction() + "\\n" + msg.getOrigin()
                + " to " + msg.getDestination() + "\"];");
        output.println("  \"n" + msg.getReplyTo() + "\" -> " + " \"n" + msg.getId() + "\";");
      }
    }
  }

  /* (non-Javadoc)
   * @see org.valabs.odisp.common.Message#cloneMessage()
   */
  public final Message cloneMessage() {
    Message result = null;
    try {
      result = (Message) this.clone();
    } catch (CloneNotSupportedException e) {
    }
    return result;
  }
}
