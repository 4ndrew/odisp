package com.novel.stdobj.console;

import com.novel.odisp.common.PollingODObject;
import com.novel.odisp.common.Message;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
/** ������ ODISP ����������� ���������� ��������� ������� � ���������
* @author �������� �. ��������
* @author (C) 2003, ��� "�����-��"
* @version $Id: ConsoleObject.java,v 1.9 2003/11/20 08:29:09 valeks Exp $
*/
public class ConsoleObject extends PollingODObject {
  /** ����� �������� ���� � ������� */
  private Thread reader;
  /** ���������� �������� ��������� 
   * @param msg ���������
   */
  public void handleMessage(Message msg) {
    logger.finest("processing " + msg);
    if (msg.getAction().equals("od_object_loaded")) {
      reader = new Thread(new Runnable() {
	  BufferedReader inp = new BufferedReader(new InputStreamReader(System.in));
	  public void run() {
	    try {
	      sleep(100);
	      System.out.print("action> ");
	      String action, tmp;
	      while ((action = inp.readLine()) != null) {
		System.out.print("destination> ");
		Message m = dispatcher.getNewMessage(action, inp.readLine(), getObjectName(), 0);
		System.out.print("params? ");
		while (!inp.readLine().equals("")) {
		  System.out.print("int|str? ");
		  tmp = inp.readLine();
		  System.out.print("value> ");
		  if (tmp.startsWith("i")) {
		    m.addField(new Integer(inp.readLine()));
		  } else {
		    m.addField(new String(inp.readLine()));
		  }
		  System.out.print("more? ");
		}
		getDispatcher().sendMessage(m);
		sleep(1);
		System.out.print("action> ");
	      }
	    } catch (IOException e) {
	      logger.info("ConsoleReader: Terminal connection lost. Quitting.");
	    } catch (InterruptedException e) {
	      logger.info("ConsoleReader: closing console");
	    }
	  }    
	});
      reader.start();
    }
    if (msg.getAction().equals("od_cleanup")) {
      if (msg.getFieldsCount() == 1) {
	cleanUp(((Integer) msg.getField(0)).intValue());
      } else {
	cleanUp(0);
      }
    } else {
      System.out.println("Received:");
      System.out.println(msg.toString());
      if (msg.getFieldsCount() > 0) {
	System.out.println("Fields dump:");
      }
      for (int i = 0; i < msg.getFieldsCount(); i++) {
	System.out.print(i + ":");
	System.out.println(msg.getField(i).toString());
      }
    }
    return;
  }
  /** ����� 
   * @param type ������� ������
   * @return ��� ��������
   */
  public int cleanUp(int type) {
    if (reader != null) {
      reader.interrupt();
    }
    doExit = true;
    return 0;
  }
  /** ����������� ������� � �������� ���������� �������
   * @param id �����
   */
  public ConsoleObject(Integer id) {
    super("console" + id);
  }
  /** ������� ������ ��������
   * @return ������ ��������
   */
  public String[] getProviding() {
    String res[] = {"console"};
    return res;
  }
  /** ������� ������ ������������ 
   * @return ������ ������������
   */
  public String[] getDepends() {
    String res[] = {
      "stddispatcher",
      "log"
    };
    return res;
  }
}
