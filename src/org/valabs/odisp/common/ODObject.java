package com.novel.odisp.common;
import java.util.*;

/** ������� ����� ����������� �������� ���� ��� ���������
* ���������� ����������� ODISP.
* @author �������� �. ��������
* @author (�) 2003, ��� "�����-��"
* @version $Id: ODObject.java,v 1.3 2003/10/06 12:38:12 valeks Exp $
*/
public abstract class ODObject extends Thread {
	protected Dispatcher dispatcher;
	private List messages;
	/** ������� ��������� ������ ��������� ����� ��������� ��������� */
	private boolean doExit;
	/** ���������� ������������� ���������� �������*/
	protected int quant = 0;
	/** ���������� ��� ������� � ���� ODISP */
	public String name;
	/** ����������� ���������������� �������� ����*/
	public ODObject(){
	    messages = new ArrayList();
	}
	/** ������ � ���������� */
	protected Dispatcher getDispatcher(){
	    return dispatcher;
	}
	/** ��������� ������� ������� ��� ����������� ����������������� ���������� ���������� ��������*/
	public void setQuant(int quant){
	    this.quant = quant;
	}
	/** ���������� ���������� ODISP ��� ������� */
	public String getObjectName(){
	    return name;
	}
	/** ������������� ���������� ��� �������� ������� */
	public void setDispatcher(Dispatcher d){
	    this.dispatcher = d;
	}
	/** ���������� ������ ��� ODISP ������� */
	public String toString(){
	    return getObjectName()+quant;
	}
	/** ���� ��������� ���������� ��������� */
	public final void run(){
	    while(doExit!=true){
		try {
		    wait();
		} catch(InterruptedException e){}
		List localMessages;
		synchronized (messages) {
		    localMessages = new ArrayList(messages);
		}
		if(localMessages != null && localMessages.size() > 0){
		    Iterator mIter = localMessages.iterator();
		    while(mIter.hasNext())
			handleMessage((Message)mIter.next());
		}
	    }
	}
	/** ��������� ���������� ��������� � ���� */
	public final void addMessage(Message msg){
	    synchronized (messages) {
		messages.add(msg);
	    }
	}
	/** ����� ������� �������� ��� ��������� ���������� ���������.
	* ��������� ������� ����������� ���� ����� ��� ���������� ������.
	*/
	public abstract void handleMessage(Message msg);
	/** ����� ���������� ��� ������� ������ ������ */
	public int cleanUp(int type){
	    doExit = true;
	    return 0;
	}
}