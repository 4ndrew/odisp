package com.novel.odisp.common;
import java.util.*;
import java.util.regex.*;

/** ������� ����� ����������� �������� ���� ��� ���������
* ���������� ����������� ODISP.
* @author �������� �. ��������
* @author (�) 2003, ��� "�����-��"
* @version $Id: ODObject.java,v 1.5 2003/10/12 14:28:32 valeks Exp $
*/
public abstract class ODObject extends Thread {
	protected Dispatcher dispatcher;
	protected List messages;
	/** ������� ��������� ������ ��������� ����� ��������� ��������� */
	protected boolean doExit;
	/** ���������� ��� ������� � ���� ODISP */
	public String name;
	/** ����������� ���������������� �������� ����*/
	public ODObject(String name){
	    messages = new ArrayList();
	    this.name = name;
	}
	/** ������ � ���������� */
	protected Dispatcher getDispatcher(){
	    return dispatcher;
	}
	/** ���������� ���������� ODISP ��� ������� */
	public String getObjectName(){
	    return name;
	}
	/** ������������� ���������� ��� �������� ������� */
	public void setDispatcher(Dispatcher d){
	    this.dispatcher = d;
	}
	/** ��������� ���������� ��������� � ���� */
	public abstract void addMessage(Message msg);
	/** ����� ������� �������� ��� ��������� ���������� ���������.
	* ��������� ������� ����������� ���� ����� ��� ���������� ������.
	*/
	protected abstract void handleMessage(Message msg);
	/** ����� ���������� ��� ������� ������ ������ */
	public abstract int cleanUp(int type);
	protected void log(String place, String msg){
	    if(false)
		System.out.println("[D] "+getObjectName()+"."+place+": "+msg);
	}
}