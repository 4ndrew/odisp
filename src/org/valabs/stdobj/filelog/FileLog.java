package com.novel.stdobj.filelog;

import com.novel.odisp.common.*;
import com.novel.stdobj.simpleconfig.SimpleConfig;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/** ������ ����������� ���������� ������������ ������� �������� ����� ��������
* @author �������� �. ��������
* @author (�) 2003, ��� "�����-��"
* @version $Id: FileLog.java,v 1.7 2003/10/22 21:22:03 valeks Exp $
*/
public class FileLog extends PollingODObject {
    PrintWriter out;
    List patterns;
    public void handleMessage(Message msg){
        logger.finest("processing "+msg);	
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
			    logger.finer("adding pattern "+s);
			}
			pfile.close();
		    } catch (FileNotFoundException e) {logger.warning("unable to open logfile.");
		    } catch (IOException e) {logger.warning("unable to read either log file or pattern file");}
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
        if(patterns == null || out == null)
	    return;
	boolean doMatch = false;
	Iterator it = patterns.iterator();
	while(it.hasNext())
	    if(Pattern.matches((String)it.next(), msg.getAction()))
		doMatch = true;
	if(doMatch){
	    out.write(new Date().toString() + " -- "+msg+"\n");
	    out.flush();
	}
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