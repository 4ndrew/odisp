package com.novel.odisp.common;

import java.util.*;
import java.util.regex.*;
import java.util.logging.Logger;

/** ������� ����� ����������� �������� ���� ��� ���������
* ���������� ����������� ODISP.
* @author �������� �. ��������
* @author (�) 2003, ��� "�����-��"
* @version $Id: ODObject.java,v 1.10 2003/10/22 21:21:33 valeks Exp $
*/
public abstract class ODObject extends Thread {
	/** ������ */
	protected Logger logger;
	/** ��������� ���������� � ���� �������� */
	protected Dispatcher dispatcher;
	/** ������ ��������� � ��������� */
	protected List messages;
	/** ������� ��������� ������ ��������� ����� ��������� ��������� */
	protected boolean doExit;
	/** Regex ����� ����������� ���������. �� ��������� ���������������� ������ �������. */
	protected String match;
	/** ���������� ��� ������� � ���� ODISP */
	public String name;
	/** �������� ����� ����������� ���������
	    @param match ����� �����
	    @return void
	*/
	protected void setMatch(String match){
	    this.match = match;
	}
	/** ����������� ���������������� �������� ����
	@param name ��� �������
	*/
	public ODObject(String name){
	    super(name);
	    messages = new ArrayList();
	    this.name = name;
	    this.match = name;
	    logger = Logger.getLogger(name);
	}
	/** ������ � ���������� 
	@return ������ �� ���������
	*/
	protected Dispatcher getDispatcher(){
	    return dispatcher;
	}
	/** ���������� ���������� ODISP ��� ������� 
	@return ODISP ��� �������
	*/
	public String getObjectName(){
	    return name;
	}
	/** ������������� ���������� ��� �������� ������� 
	@param d ��������� ���������� � ���� ��������
	@return void
	*/
	public void setDispatcher(Dispatcher d){
	    this.dispatcher = d;
	}
	/** ��������� ���������� ��������� � ���� 
	@param msg ��������� ��� ����������
	@return void
	*/
	public abstract void addMessage(Message msg);
	/** ���������� ������ ��������� � ���� 
	@param messages ������ ��������� ��� ����������
	@return void
	*/
	public void addMessages(List messages){
	    Iterator it = messages.iterator();
	    while(it.hasNext())
		addMessage((Message)it.next());
	}	
	/** ����� ������� �������� ��� ��������� ���������� ���������.
	��������� ������� ����������� ���� ����� ��� ���������� ������.
	@param msg ��������� ��� ���������
	@return void
	*/
	protected abstract void handleMessage(Message msg);
	/** ����� ���������� ��� ������� ������ ������ 
	@param type ������� ������
	@return void
	*/
	public abstract int cleanUp(int type);
	/** ����� ���������� ��������� �� �����
	@param place ������� ���� � �������� ��������� ���������
	@param msg ���������
	*/
	protected void log(String place, String msg){
		logger.fine(getObjectName()+"."+place+": "+msg);
	}
	/** ������ ������ ������������
	    @return ������ ������������
	*/
	public String[] getDepends(){
	    String res[] = {};
	    return res;
	}
	/** ������ �������� ��������������� ��������
	@return ������ ��������
	*/
	public abstract String[] getProviding();
}