package com.novel.odisp.common;
import java.util.*;
import java.util.regex.*;

/** ������� ����� ����������� �������� ���� ��� ���������
* ���������� ����������� ODISP � ���������� ���������� ��������� �� ���� �������.
* @author �������� �. ��������
* @author (�) 2003, ��� "�����-��"
* @version $Id: CallbackODObject.java,v 1.2 2003/10/12 20:02:49 valeks Exp $
*/
public abstract class CallbackODObject extends ODObject {
	private Map handlers; 
	/** ������� ��������� ������ ��������� ����� ��������� ��������� */
	public CallbackODObject(String name){
	    super(name);
	    handlers = new HashMap();

	}
	/** ���������� ������ ����������� �������
	@param message ��������� �������������� ������������
	@param handler �����-���������� ���������	
	*/
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
	    if(!Pattern.matches(match, msg.getDestination()))
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