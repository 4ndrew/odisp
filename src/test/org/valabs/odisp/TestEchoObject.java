package test.org.valabs.odisp;

import junit.framework.TestCase;

import org.valabs.odisp.common.Message;
import org.valabs.stdobj.echo.EchoObject;


/** Тестовый тест для тестового объекта.
 * Проверка принципиальной возможности создания тестов для объектов ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Алексеев Валентин А.</a>
 * @version $Id: TestEchoObject.java,v 1.2 2004/12/03 09:48:40 valeks Exp $
 */
public class TestEchoObject extends TestCase {
  /** Тестирование на корректность эха. */
  public void testEcho() {
    class DispatcherTest extends DispatcherFake {
      public void send(Message msg) {
        if (msg.getAction().equals("echo_reply")) {
          if (msg.getField("0").equals("Hello, World!")) {
          } else {
            signalException(new Exception("Wrong message text received"));
          }
        } else {
          signalException(new Exception("Wrong message received"));
        }
      }
    };
    
    DispatcherTest disp = new DispatcherTest();
    
    EchoObject eo = new EchoObject(new Integer(1234));
    eo.setDispatcher(disp);
    Message m = disp.getNewMessage();
    m.setAction("echo");
    m.addField("0", "Hello, World!");
    eo.handleMessage(m);
  }
  
  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestEchoObject.class);
  }

}
