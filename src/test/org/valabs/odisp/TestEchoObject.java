package test.org.valabs.odisp;

import junit.framework.TestCase;

import org.valabs.odisp.common.Message;
import org.valabs.stdobj.echo.EchoObject;


/** �������� ���� ��� ��������� �������.
 * �������� �������������� ����������� �������� ������ ��� �������� ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">�������� �������� �.</a>
 * @version $Id: TestEchoObject.java,v 1.3 2005/01/26 08:22:53 valeks Exp $
 */
public class TestEchoObject extends TestCase {
  /** ������������ �� ������������ ���. */
  public void testEcho() {
    class DispatcherTest extends DispatcherFake {
      public void send(Message msg) {
        if (!msg.getAction().equals("echo_reply")) {
          signalException(new Exception("unknown message received"));
        }
      }
    };
    
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
