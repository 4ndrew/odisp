/*
 * $Id: ConsoleReader.java,v 1.1 2003/10/02 23:16:31 valeks Exp $
 *	ConsoleReader.java
 *	ODISP Object that implements console interaction with Dispatcher
 *      (C) 2003, JTT Novel-IL 
 */
package com.novel.stdobj.console;
import com.novel.odisp.common.*;
import java.io.*;

public class ConsoleReader extends Thread {
    ConsoleObject parent;
    BufferedReader inp = new BufferedReader(new InputStreamReader(System.in));
    public ConsoleReader(ConsoleObject parent){
	super("ConsoleReader");
	this.parent = parent;
	start();
    }
    
    public void run(){
	try {
	    sleep(100);
	    System.out.print("action> ");
	    String action, tmp;
	    while((action = inp.readLine()) != null){
		System.out.print("destination> ");
		Message m = parent.getDispatcher().getNewMessage(action, inp.readLine(), parent.toString(), 0);
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
		parent.getDispatcher().sendMessage(m);
		sleep(1);
	        System.out.print("action> ");
	    }
	} catch(IOException e) {
	    System.err.println("ConsoleReader: Terminal connection lost. Quitting.");
	} catch(InterruptedException e){
	    System.out.println("ConsoleReader: closing console");
	}
    }    
}