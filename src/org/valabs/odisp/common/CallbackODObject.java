package com.novel.odisp.common;
import java.util.*;
import java.util.regex.*;

/** ������� ����� ����������� �������� ���� ��� ���������
* ���������� ����������� ODISP � ���������� ���������� ��������� �� ���� �������.
* @author �������� �. ��������
* @author (�) 2003, ��� "�����-��"
* @version $Id: CallbackODObject.java,v 1.1 2003/10/07 11:06:17 valeks Exp $
*/
public abstract class CallbackODObject extends ODObject {
	private Map handlers; 
	/** ������� ��������� ������ ��������� ����� ��������� ��������� */
	public CallbackODObject(String name){
	    super(name);
	    handlers = new HashMap();

	}
	protected void addHandler(String message, MessageHandler handler){
	    if(handlers.containsKey(message))
		return;
	    log("addHandler","registered handler for "+message);
	    handlers.put(message, handler);
	}
	/** ���� ��������� ���������� ��������� */
	public final void run(){
	    registerHandlers();	
	    // we do not need message loop.
	}
	/** ��������� ���������� ��������� � ���� */
	public final void addMessage(Message msg){
	    if(!Pattern.matches(msg.getDestination(), getObjectName()))
		return;
	    handleMessage(msg);
	}
	protected final void handleMessage(Message msg){
	    if(handlers.containsKey(msg.getAction())){
		((MessageHandler)handlers.get(msg.getAction())).messageReceived(msg);
	    } else {
		log("handleMessage","there is no handler for message "+msg.getAction());
	    }
	}
	/** ����� ���������� ��� ������� ������ ������ */
	public int cleanUp(int type){
	    doExit = true;
	    return 0;
	}
	/** ����������� ������ ������ ������������� ���� ����� ��� ����������� ������������  */
	protected abstract void registerHandlers();
}