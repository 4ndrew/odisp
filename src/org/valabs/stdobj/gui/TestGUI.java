package com.novel.stdobj.gui;

import com.novel.odisp.common.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** Объект, реализующие функции GUI-менеджера, то есть основного окна (?)
* программы (тестовая версия).
* @author Andrew A. Porohin
* @author (C) 2003 НПП "Новел-ИЛ"
* @version $Id: TestGUI.java,v 1.1 2003/10/13 21:43:44 dron Exp $
*/
public class TestGUI extends CallbackODObject {
   private JTextField txt1 = new JTextField(2);
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
                    log("messageReceived", "");
                    cleanUp();
                  }
                });
   }
   
   /** Зачистка
   */
   public int cleanUp() {
      return 0;
   }
   
   /** Конструктор
   */
   public TestGUI(Integer id) {
     super("testGUI"+id);
     JFrame mainFrame; /* Основной фрем программы */
     JButton buttonOk; /* Просто тестовый батон */
     ButtonOkListener bol = new ButtonOkListener();
     
     buttonOk = new JButton("Ok :-)");
     buttonOk.addActionListener(bol);
     
     mainFrame = new JFrame("TestGUI::mainFrame");
     // temporarily...
     mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     Container cn = mainFrame.getContentPane();
     cn.setLayout(new FlowLayout());
     cn.add(buttonOk);
     cn.add(txt1);
     mainFrame.setSize(400, 400);
     mainFrame.setVisible(true);
   }
}
