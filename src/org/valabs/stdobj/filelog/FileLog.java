package com.novel.stdobj.filelog;
import com.novel.odisp.common.*;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/** Объект реализующий простейшую журнализацию событий согласно файлу шаблонов
* @author Валентин А. Алексеев
* @author (С) 2003, НПП "Новел-ИЛ"
* @version $Id: FileLog.java,v 1.2 2003/10/04 12:56:20 valeks Exp $
*/
public class FileLog extends ODObject {
	public String name = "filelog";
	private int valuesToConfigure = 2;
	private String logFile, patternFile;
	private int logFileMsgId, patternFileMsgId;
	PrintWriter out;
	String[] patterns;
	public void handleMessage(Message msg){
// FileLog wants to receive _any_ message 
//            if(!Pattern.matches(msg.getDestination(), toString()))
//                return;	
	    if(valuesToConfigure == 0){ // finaly we got all params we need to start -- fire up
		try {
		    File hLogFile = new File(logFile);
		    if(!hLogFile.exists())
			hLogFile.createNewFile();
		    out = new PrintWriter(new FileWriter(hLogFile, true));
		    BufferedReader pfile = new BufferedReader(new FileReader(patternFile));
		    String s;
		    List readedPatterns = new ArrayList();
		    while((s = pfile.readLine()) != null)
			readedPatterns.add(s);
		    patterns = new String[readedPatterns.size()];
		    patterns = (String[])readedPatterns.toArray();
		    pfile.close();
		    valuesToConfigure--;
		} catch (FileNotFoundException e) {System.err.println("[w] unable to open logfile "+logFile);}
		  catch (IOException e) {System.err.println("[w] unable to read either log file or pattern file");}
	    }
	    if(msg.getAction().equals("od_cleanup"))
		cleanUp(((Integer)msg.getField(0)).intValue());
	    else if(msg.getAction().equals("od_object_loaded") && msg.getDestination().equals(toString())){ // we are starting up -- request config values
		Message m = dispatcher.getNewMessage("get_value","config.*", toString(), 0);
		m.addField(new String("log_logfile"));
		logFileMsgId = m.getId();
		dispatcher.sendMessage(m);
		m = dispatcher.getNewMessage("get_value","config.*", toString(), 1);
		m.addField(new String("log_patternfile"));
		patternFileMsgId = m.getId();
		dispatcher.sendMessage(m);
	    } else if(msg.getAction().equals("config_value") && msg.getDestination().equals(toString())){ // we got config value reply
		if(msg.getFieldsCount() == 1){
			if(msg.getReplyTo() == logFileMsgId) 
			    logFile = (String)msg.getField(0);
			if(msg.getReplyTo() == patternFileMsgId) 			    
			    patternFile = (String)msg.getField(0);
		    valuesToConfigure--;
		}
	    } else {
		for(int i = 0; i< patterns.length; i++){
		    if(Pattern.matches(patterns[i], msg.getAction()))
			out.write("---\n"+new Date().toString() + " -- "+msg);
		}
	    }
	    return;
	}
	public int cleanUp(int type){
//	    try {
	    out.close();
/*	    } catch(IOException e){
		System.err.println("[w] unable to close log file");
	    }*/
	    return 0;
	}
	public String getObjectName(){return name;}
}