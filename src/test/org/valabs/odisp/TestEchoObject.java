/* ODISP -- Message Oriented Middleware
 * Copyright (C) 2003-2005 Valentin A. Alekseev
 * Copyright (C) 2003-2005 Andrew A. Porohin 
 * 
 * ODISP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.1 of the License.
 * 
 * ODISP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ODISP.  If not, see <http://www.gnu.org/licenses/>.
 */
package test.org.valabs.odisp;

import junit.framework.TestCase;

import org.valabs.odisp.common.Message;
import org.valabs.stdobj.echo.EchoObject;


/** �������� ���� ��� ��������� �������.
 * �������� �������������� ����������� �������� ������ ��� �������� ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">�������� �������� �.</a>
 * @version $Id: TestEchoObject.java,v 1.4 2005/07/22 13:06:57 dron Exp $
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
