package com.novel.stdobj.filelog;

import com.novel.odisp.common.*;
import com.novel.stdobj.simpleconfig.SimpleConfig;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/** Объект реализующий простейшую журнализацию событий согласно файлу шаблонов
* @author Валентин А. Алексеев
* @author (С) 2003, НПП "Новел-ИЛ"
* @version $Id: FileLog.java,v 1.6 2003/10/14 09:41:08 valeks Exp $
*/
public class FileLog extends PollingODObject {
    PrintWriter out;
    List patterns;
    public void handleMessage(Message msg){
        log("handleMessage",""+msg);	
        if(msg.getAction().equals("od_cleanup") && msg.getDestination().equals(getObjectName())){
	    cleanUp(((Integer)msg.getField(0)).intValue());
	    return;
	}
	if(msg.getAction().equals("od_object_loaded") && msg.getDestination().equals(getObjectName())){
	    setMatch(".*");
	    Message m = dispatcher.getNewMessage("od_acquire","stddispatcher", getObjectName(), 0);
	    m.addField(new String("com.novel.stdobj.simpleconfig.SimpleConfig"));
	    m.addField(new Boolean(true));
	    dispatcher.sendMessage(m);
	    return;
	}
	if(msg.getAction().equals("resource_acquired") && msg.getDestination().equals(getObjectName())){ // we acquired SimpleConfig resource
	    if(msg.getFieldsCount() == 2){
		String cl_n = (String)msg.getField(0);
		if(cl_n.startsWith("com.novel.stdobj.simpleconfig.SimpleConfig")){
		    SimpleConfig scfg = (SimpleConfig)msg.getField(1);
		    scfg.readConfig("simpleconfig.dat");
		    try {
		        File hLogFile = new File(scfg.getValue("log_logfile"));
		        if(!hLogFile.exists())
		    	    hLogFile.createNewFile();
			out = new PrintWriter(new FileWriter(hLogFile, true));
			BufferedReader pfile = new BufferedReader(new FileReader(scfg.getValue("log_patternfile")));
			String s;
			patterns = new ArrayList();
			while((s = pfile.readLine()) != null){
			    patterns.add(s);
			    log("patterns","adding pattern "+s);
			}
			pfile.close();
		    } catch (FileNotFoundException e) {System.err.println("[w] unable to open logfile.");
		    } catch (IOException e) {System.err.println("[w] unable to read either log file or pattern file");}
		    Message[] m = {
			dispatcher.getNewMessage("od_release", "stddispatcher", getObjectName(), msg.getId()),
			dispatcher.getNewMessage("od_remove_dep", "stddispatcher", getObjectName(), 0)
		    };
		    m[0].addField(msg.getField(0));
		    m[0].addField(scfg);
		    m[1].addField("com.novel.stdobj.simpleconfig.SimpleConfig");
		    dispatcher.sendMessages(m);
		}
	    }
	    return;
	}
	log("doLog","1");
        if(patterns == null || out == null)
	    return;
	log("doLog","2");	    
	boolean doMatch = false;
	Iterator it = patterns.iterator();
	while(it.hasNext())
	    if(Pattern.matches((String)it.next(), msg.getAction()))
		doMatch = true;
	log("doLog","3");
	if(doMatch){
	    out.write(new Date().toString() + " -- "+msg+"\n");
	    out.flush();
	}
	log("doLog","4");
    }
    public int cleanUp(int type){
	if(out != null)
	    out.close();
	doExit = true;
	return 0;
    }
    public FileLog(Integer id){
	super("log"+id);
    }
    public String[] getProviding(){
	String res[] = {
	    "log"
	};
	return res;
    }
    public String[] getDepends(){
	String res[] = {
	    "stddispatcher",
	    "com.novel.stdobj.simpleconfig.SimpleConfig"
	};
	return res;
    }
}