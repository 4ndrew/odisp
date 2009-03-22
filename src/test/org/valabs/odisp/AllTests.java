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

import junit.framework.Test;
import junit.framework.TestSuite;


/** Запуск всех тестов для ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Алексеев Валентин А.</a>
 * @version $Id: AllTests.java,v 1.2 2004/12/02 22:54:35 valeks Exp $
 */
public class AllTests {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(AllTests.suite());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite("Tests for org.valabs.odisp");
    //$JUnit-BEGIN$
    suite.addTestSuite(TestSessionManager.class);
    suite.addTestSuite(TestEchoObject.class);
    //$JUnit-END$
    return suite;
  }
}
