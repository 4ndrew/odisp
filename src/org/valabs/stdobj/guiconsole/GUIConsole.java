package com.novel.stdobj.guiconsole;
import com.novel.odisp.common.*;
import java.util.regex.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

/** Объект ODISP реализующий графический интерфейс доступа к менеджеру
 * @author Андрей А. Порохин
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: GUIConsole.java,v 1.6 2003/10/22 21:22:03 valeks Exp $
 */
public class GUIConsole extends PollingODObject {
   JFrame mFrame;
   JTextPane mTextArea1;
   JButton mButton1;
   JButton mButton2;
   JComboBox mEdit1;
   JComboBox objectList;
   JComboBox action;
   HTMLDocument doc;
   public void handleMessage(Message msg) {
      log("handleMessage", "processing "+msg);
	if(msg.getAction().equals("od_cleanup")) {
         cleanUp(((Integer)msg.getField(0)).intValue());
      } else if(msg.getAction().equals("od_object_loaded")){
        mFrame = new JFrame("GUIConsole");
    	mFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mFrame.setSize(600, 400);
	objectList = new JComboBox();
	action = new JComboBox();
	action.setEditable(true);
        mTextArea1 = new JTextPane();
        doc = new HTMLDocument();
        mTextArea1.setDocument(doc);

        mEdit1 = new JComboBox();
	mEdit1.setEditable(true);
        mButton1 = new JButton("Send");
        mButton2 = new JButton("Clear");
     
        mButton1.addActionListener(new ActionListener() {
    	    public void actionPerformed(ActionEvent event) {
        	parseCommand((String)mEdit1.getSelectedItem());
            }
        });
     
        mButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               mTextArea1.setText(new String(""));
            }
    	});

        JPanel mPanel1 = new JPanel();
      
        // mPanel1.setLayout(new GridLayout(1, 1));
        mPanel1.add(BorderLayout.CENTER, new JScrollPane(mTextArea1, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                       JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        JPanel mPanel2 = new JPanel();
        mPanel1.setLayout(new BoxLayout(mPanel1, BoxLayout.X_AXIS));
	mPanel2.add(new JLabel("Object"));
	mPanel2.add(objectList);
	mPanel2.add(new JLabel("Action"));
	mPanel2.add(action);
        mPanel2.add(mEdit1);
        mPanel2.add(mButton1);
        mPanel2.add(mButton2);

        Container cp = mFrame.getContentPane();
        cp.add(BorderLayout.CENTER, mPanel1);
        cp.add(BorderLayout.SOUTH, mPanel2);
        mFrame.pack();
	Message m = dispatcher.getNewMessage("od_list_objects", "stddispatcher", getObjectName(), 0);
	dispatcher.sendMessage(m);
      } else if(msg.getAction().equals("object_list")){
        if(msg.getFieldsCount() == 1 && msg.getField(0) instanceof java.util.List){
    	    objectList.removeAllItems();
	    java.util.List objs = (java.util.List)msg.getField(0);
	    Iterator it = objs.iterator();
	    while(it.hasNext())
		objectList.addItem(it.next());
	    mFrame.setVisible(true);
	}
      } else {
        try {
                Document doc = mTextArea1.getDocument();
                doc.insertString(doc.getLength(),"<html>",null);
                doc.insertString(doc.getLength(),"<b>Received:</b>\n",null);
                doc.insertString(doc.getLength(),"<u>"+msg.toString()+"</u>\n",null);
                if(msg.getFieldsCount() > 0) {
                        doc.insertString(doc.getLength(),"Fields dump:\n", null);
                }
                for(int i = 0;i<msg.getFieldsCount();i++) {
                        doc.insertString(doc.getLength(),i+":<b>", null);
                        doc.insertString(doc.getLength(),msg.getField(i).toString()+"</b>\n", null);
                }
                doc.insertString(doc.getLength(),"</html>", null);
         } catch (BadLocationException e){}
      }
      return;
   }

   /* CleanUp
   */
   public int cleanUp(int type) {
      mFrame.setVisible(false);
      mFrame.hide();
      try {
         synchronized(this) {
            wait(1000);
         }
      } catch(InterruptedException e) {
         logger.warning("Exception while wait: "+e);
      }
      synchronized(mFrame) {
         mFrame.dispose();
      }
      doExit = true;
      return 0;
   }
   private void addUniqItem(JComboBox box, String item){
        for(int i = 0; i < box.getItemCount(); i++)
                if(((String)box.getItemAt(i)).equals(item))
                        return;
        box.addItem(item);
   }

   private void parseCommand(String stCmd) {
      String stContent[] = {};
      if(stCmd != null)
        stContent = stCmd.split(" ");
/*      for(int i=0; i < stContent.length; i++) {
         mTextArea1.setText(mTextArea1.getText()+"["+i+"]"+stContent[i]+"\n");
      }*/
      if(stContent.length > 1) {
         Message newMsg = dispatcher.getNewMessage((String)action.getSelectedItem(), (String)objectList.getSelectedItem(), getObjectName(), 0);
         if(stContent.length % 2 == 0) {
            for(int i=0; i < Math.round(stContent.length/2); i++) {
               if(stContent[i+0].startsWith("i")) {
                  try {
                     newMsg.addField(new Integer(stContent[i + 1]));
                  } catch(NumberFormatException e) {
//                     mTextArea1.setText(mTextArea1.getText()+"Exception while converting string to integer: "+e+"\n");
                  }
               }
               else {
                  newMsg.addField(new String(stContent[i + 1]));
               }
            }
         }
         getDispatcher().sendMessage(newMsg);
	 addUniqItem(action, (String)action.getSelectedItem());
	 addUniqItem(mEdit1, (String)mEdit1.getSelectedItem());
      }
   }
   
   public GUIConsole(Integer id) {
      super("guiconsole"+id);
   }
   
   /** Список предоставляемых ресурсов 
   */
   public String[] getProviding() {
      String res[] = {"testGUI"};
      return res;
   }
   
   /** Список необходимых ресурсов
   */
   public String[] getDepends() {
      String res[] = {"stddispatcher"};
      return res;
   }

}
