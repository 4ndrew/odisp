package com.novel.stdobj.gui;

import com.novel.odisp.common.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** Объект, реализующие функции GUI-менеджера, то есть основного окна (?)
* программы (тестовая версия).
* @author Андрей А. Порохин
* @author Валентин А. Алексеев
* @author (C) 2003 НПП "Новел-ИЛ"
* @version $Id: TestGUI.java,v 1.4 2003/10/14 13:59:07 dron Exp $
*/
public class TestGUI extends CallbackODObject {
   private JTextField txt1 = new JTextField(20);
   private JFrame mainFrame;
   public class ButtonOkListener implements ActionListener {
      public void actionPerformed(ActionEvent e) {
         String name = ((JButton)e.getSource()).getText();
         txt1.setText(name);
      }
   }
   /** Регистрация сообщений, которые будут приниматься.
   */
   protected void registerHandlers() {
     addHandler("od_cleanup",
                new MessageHandler() {
                  public void messageReceived(Message msg) {
                    txt1.setText("Ops... Shutdown!");
                    log("messageReceived", "");
                    cleanUp(((Integer)msg.getField(0)).intValue());
                  }
                });
   }

   /** Зачистка
   */
   public int cleanUp(int type) {
      mainFrame.hide();
      try {
      	 wait(1000);
      } catch(InterruptedException e) {
      	 System.err.println("Exception: "+e);
      }
      mainFrame.dispose();
      return 0;
   }

   /** Конструктор
   */
   public TestGUI(Integer id) {
     super("testGUI"+id);
     JButton buttonOk; /* Просто тестовый батон */
     ButtonOkListener bol = new ButtonOkListener();

     buttonOk = new JButton("Ok :-)");
     buttonOk.addActionListener(bol);

     mainFrame = new JFrame("TestGUI::mainFrame");
     // temporarily...
     mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
     Container cn = mainFrame.getContentPane();
     cn.setLayout(new FlowLayout());
     cn.add(buttonOk);
     cn.add(txt1);
     mainFrame.setSize(400, 400);
     mainFrame.setVisible(true);
   }
   public String[] getProviding(){
	String[] res = {"testGUI"};
	return res;
   }
   public String[] getDepends(){
	String[] res = {"stddispatcher"};
	return res;
   }
}
