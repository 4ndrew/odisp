package com.novel.stdobj.simpleconfig;
import com.novel.odisp.common.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

/** ������ ODISP ����������� ������ � ���������������� ������ ������� [���]=[��������]
* @author �������� �. ��������
* @author (C) 2003, ��� "�����-��"
* @version $Id: SimpleConfig.java,v 1.5 2003/10/12 20:06:15 valeks Exp $
*/
public class SimpleConfig implements Resource {
    String cfgName;
    Map contents = new HashMap();
    /** ������ ����������������� ����� � ������
	@param cfgName ��� ����� ������������
	@return void
    */
    public void readConfig(String cfgName){
	System.out.println("[D] SimpleConfig.readConfig("+cfgName+")");    
        try {
	    BufferedReader in = new BufferedReader(new FileReader(cfgName));
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
	} catch(IOException e){}
    }
    /** ���������� �������� ���������� �� ����������������� ����� 
	@param name ��� ���������
	@return �������� ��������� ��� '-undef-' � ������ ���� �������� �� ���������
    */
    public String getValue(String name){
	System.out.println("[D] SimpleConfig.getValue("+name+")");
	if(!contents.containsKey(name))
	    return "-undef-";
	return (String)contents.get(name);
    }
    /** ���������� �������� ���������� �� ����������������� ����� � ������ �������� �� ���������
	@param name ��� ���������
	@param defaultValue �������� �� ���������
	@return �������� ��������� ��� defaultValue ���� �������� �� ���������
    */
    public String getValue(String name, String defaultValue){
	if(getValue(name).equals("-undef-"))
	    return defaultValue;
	return getValue(name);
    }
    public int cleanUp(int type){
	return 0;
    }
    public int getMaxReferenceCount(){
	return 0;
    }
}