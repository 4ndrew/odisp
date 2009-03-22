package test.org.valabs.odisp;

import junit.framework.TestCase;

import org.valabs.odisp.common.Message;
import org.valabs.stdobj.echo.EchoObject;


/** Тестовый тест для тестового объекта.
 * Проверка принципиальной возможности создания тестов для объектов ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Алексеев Валентин А.</a>
 * @version $Id: TestEchoObject.java,v 1.4 2005/07/22 13:06:57 dron Exp $
 */
public class TestEchoObject extends TestCase {
  /** Тестирование на корректность эха. */
  public void testEcho() {
    class DispatcherTest extends DispatcherFake {
      public void send(Message msg) {
        if (!msg.getAction().equals("echo_reply")) {
          signalException(new Exception("unknown message received"));
        }
      }
    }
    
    DispatcherTest disp = new DispatcherTest();
    
    EchoObject eo = new EchoObject();
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
