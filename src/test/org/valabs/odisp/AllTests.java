package test.org.valabs.odisp;

import junit.framework.Test;
import junit.framework.TestSuite;


/** Запуск всех тестов для ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Алексеев Валентин А.</a>
 * @version $Id: AllTests.java,v 1.1 2004/12/02 22:10:10 valeks Exp $
 */
public class AllTests {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(AllTests.suite());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite("Tests for org.valabs.odisp");
    //$JUnit-BEGIN$
    suite.addTestSuite(TestSessionManager.class);
    //$JUnit-END$
    return suite;
  }
}
