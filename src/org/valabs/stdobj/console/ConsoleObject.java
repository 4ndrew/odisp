package com.novel.stdobj.console;
import com.novel.odisp.common.*;
import java.util.regex.Pattern;
import java.io.*;
/** Объект ODISP реализующий консольный интерфейс доступа к менеджеру
* @author Валентин А. Алексеев
* @author (C) 2003, НПП "Новел-ИЛ"
* @version $Id: ConsoleObject.java,v 1.6 2003/10/21 12:29:33 valeks Exp $
*/
public class ConsoleObject extends PollingODObject {
	private Thread reader;
	public void handleMessage(Message msg){
	    log("handleMessage","processing "+msg);
            if(msg.getAction().equals("od_object_loaded")){
	        reader = new Thread(new Runnable() {
	        BufferedReader inp = new BufferedReader(new InputStreamReader(System.in));
	        public void run(){
		        try {
		                sleep(100);
		                System.out.print("action> ");
		                String action, tmp;
		                while((action = inp.readLine()) != null){
			                System.out.print("destination> ");
			                Message m = dispatcher.getNewMessage(action, inp.readLine(), getObjectName(), 0);
			                System.out.print("params? ");
			                while(!inp.readLine().equals("")){
			                        System.out.print("int|str? ");
			                        tmp = inp.readLine();
			                        System.out.print("value> ");
			                        if(tmp.startsWith("i"))
				                        m.addField(new Integer(inp.readLine()));
			                        else 
				                        m.addField(new String(inp.readLine()));
			                        System.out.print("more? ");
			                }
			                getDispatcher().sendMessage(m);
			                sleep(1);
			                System.out.print("action> ");
		                }
		        } catch(IOException e) {
		                System.err.println("ConsoleReader: Terminal connection lost. Quitting.");
		        } catch(InterruptedException e){
		                System.out.println("ConsoleReader: closing console");
		        }
	        }    
	        });
	        reader.start();
            }
	    if(msg.getAction().equals("od_cleanup"))
		cleanUp(((Integer)msg.getField(0)).intValue());
	    else {
		System.out.println("Received:");
		System.out.println(msg.toString());
		if(msg.getFieldsCount()>0)
		System.out.println("Fields dump:");
		for(int i = 0;i<msg.getFieldsCount();i++){
		    System.out.print(i+":");
		    System.out.println(msg.getField(i).toString());
		}
	    }
	    return;
	}
    public int cleanUp(int type){
	if(reader != null)
	    reader.interrupt();
	doExit = true;
	return 0;
    }
    public ConsoleObject(Integer id){
	super("console"+id);
    }
    public String[] getProviding(){
	String res[] = {"console"};
	return res;
    }
    public String[] getDepends(){
	String res[] = {
	    "stddispatcher",
	    "log"
	};
	return res;
    }
}