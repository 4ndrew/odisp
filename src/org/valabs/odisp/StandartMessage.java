package com.novel.odisp;

import com.novel.odisp.common.*;
import java.util.*;
import java.io.Serializable;

/** ���������� ������������ ��������� ��� ������������ ���������� ODISP
 * @author �������� �. ��������
 * @author (C) 2003, ��� "�����-��"
 * @version $Id: StandartMessage.java,v 1.5 2003/11/10 13:54:34 valeks Exp $
 */
public class StandartMessage implements Message, Serializable {
	/** ���������� ���������� ������� ��������� */
	static int id = 0;
	private int my_id;
	private List fields = new ArrayList();
	private String action, destination, origin;
	private int inReplyTo;
	/** ���������� ������������ ���������
	 * @param action �������� ������� ����� ���������
	 * @param destination ������� ���������
	 * @param origin ����������� ���������
	 * @param inReplyTo ������������� ��������� �� ������� ������������ �����
	 */
	public StandartMessage(String action, String destination, String origin, int inReplyTo){
		this.action = action;
		this.destination = destination;
		this.inReplyTo = inReplyTo;
		this.origin = origin;
		my_id = id++;
	}
	/** ���������� ������������� ������� � ���� ���������
	 * @param field ������ ������� ����� �������� ���������
	 */
	public void addField(Object field){
		fields.add(field);
	}
	/** ������� ������������ � ���� ��������� ������� �� �������
	 * @param field ������ �������
	 */
	public Object getField(int field){
		return fields.get(field);
	}
	/** ���������� �������� ������� ����� ��������� */
	public String getAction() { return action; }
	/** ���������� �������� ��������� */
	public String getDestination() { return destination; }
	/** ���������� ������ ��������� */
	public String getOrigin() {return origin;}
	/** ���������� ������������� ��������� �� ������� ������������ ����� */
	public int getReplyTo() {return inReplyTo;}
	/** ���������� ���������� ����� � ��������� */
	public int getFieldsCount() { return fields.size();}
	/** ���������� ���������� ������������� ��������� */
	public int getId(){ return my_id; };
	/** ������������� ��������� � ���� ��������� ������ 
	 * @return �������� ������������� ���������
	*/
	public String toString(){
	    return "stdmessage id="+my_id+" replyto="+inReplyTo+" action=" + action + ", destination=" + destination + ", origin="+origin+", fields.size()=" + fields.size();
	}
}