package test.org.valabs.odisp;

import junit.framework.TestCase;

import org.doomdark.uuid.UUID;
import org.valabs.odisp.SessionManager;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;
import org.valabs.stdmsg.StandartMessage;


/** Набор тестов для менеджера сессий.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Алексеев Валентин А.</a>
 * @version $Id: TestSessionManager.java,v 1.4 2005/07/22 16:05:53 valeks Exp $
 */
public class TestSessionManager extends TestCase {
  /** Тестирование простейшего случая -- обработчик на одиночное сообщение. */
  public void testSimpleHandler() {
    SessionManager sm = SessionManager.getSessionManager();
    MessageHandler mh = new MessageHandler() {
      public void messageReceived(final Message msg) {
        /* не используется */
      }
    };
    sm.addMessageListener(UUID.getNullUUID(), mh);
    Message m = new StandartMessage();
    m.setReplyTo(UUID.getNullUUID());
    assertEquals(true, sm.processMessage(m));
    assertEquals(false, sm.processMessage(m));
  }
  
  /** Тестирование возможности задания обработчика с ручным удалением. */
  public void testMultiplyCalls() {
    SessionManager sm = SessionManager.getSessionManager();
    MessageHandler mh = new MessageHandler() {
      public void messageReceived(final Message msg) {
        /* не используется */
      }
    };
    sm.addMessageListener(UUID.getNullUUID(), mh, true);
    Message m = new StandartMessage();
    m.setReplyTo(UUID.getNullUUID());
    assertEquals(true, sm.processMessage(m) && sm.processMessage(m));
    sm.removeMessageListener(UUID.getNullUUID(), mh);
    assertEquals(false, sm.processMessage(m));
  }
  
  /** Тестирование на возможность модификации данных о сессиях изнутри обработчика, вызванного
   * SessionManager'ом (в память о существовавшей ошибке). */
  public void testModifySMFromHandler() {
    SessionManager sm = SessionManager.getSessionManager();
    MessageHandler mh = new MessageHandler() {
      public void messageReceived(final Message msg) {
        SessionManager.getSessionManager().addMessageListener(UUID.getNullUUID(), new MessageHandler() {
          public void messageReceived(final Message _msg) {
            /* не используется */
          }
        });
      }
    };
    sm.addMessageListener(UUID.getNullUUID(), mh);
    Message m = new StandartMessage();
    m.setReplyTo(UUID.getNullUUID());
    assertEquals(true, sm.processMessage(m));
    assertEquals(true, sm.processMessage(m));
    assertEquals(false, sm.processMessage(m));
  }
  
  public static void main(String[] args) {
    junit.textui.TestRunner.run(TestSessionManager.class);
  }

}
