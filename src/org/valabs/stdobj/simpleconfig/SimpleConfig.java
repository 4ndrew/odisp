package com.novel.stdobj.simpleconfig;
import com.novel.odisp.common.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

/** Объект ODISP реализующий доступ к конфигурационным файлам формата [имя]=[значение]
* @author Валентин А. Алексеев
* @author (C) 2003, НПП "Новел-ИЛ"
* @version $Id: SimpleConfig.java,v 1.3 2003/10/07 11:13:48 valeks Exp $
*/
public class SimpleConfig extends PollingODObject {
	private Map contents = new HashMap();
	public String name = "config";
	public void handleMessage(Message msg){
	    log("handleMessage","processing "+msg);
	    if(msg.getAction().equals("od_object_loaded") || msg.getAction().equals("reload")){
		contents.clear();
		try {
		    BufferedReader in = new BufferedReader(new FileReader("simpleconfig.dat"));
		    String s;
		    Pattern p = Pattern.compile("^(\\w+)=(.*)");
		    while((s = in.readLine()) != null){
			if(s.startsWith("#"))
			    continue;
			Matcher m = p.matcher(s);
			m.find();
			if(m.groupCount() == 2){
			    contents.put(m.group(1), m.group(2));
			} else {
			    System.err.println("[w] syntax error in line '"+s+"'. line ignored.");
			}
		    }
		    in.close();
		} catch (FileNotFoundException e) {System.err.println("[w] SimpleConfig was unable to find data file simpleconfig.dat");}
		  catch (IOException e){System.err.println("[e] error while reading config file simpleconfig.dat");}
	    }
	    if(msg.getAction().equals("get_value")){
		if(msg.getFieldsCount() == 1){
		    String key = (String)msg.getField(0);
		    if(contents.containsKey(key)){
			Message m = dispatcher.getNewMessage("config_value", msg.getOrigin(), toString(), msg.getId());
			m.addField(contents.get(key));
			dispatcher.sendMessage(m);
		    }
		}
	    }
	}
	public int cleanUp(int type){
	    return 0;
	}
	public SimpleConfig(Integer id){
	    super("config"+id);
	}
}