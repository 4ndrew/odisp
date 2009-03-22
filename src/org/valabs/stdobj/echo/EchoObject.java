package org.valabs.stdobj.echo;

import java.util.HashMap;
import java.util.Map;

import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;
import org.valabs.odisp.common.StandartODObject;

/** Простейший ODISP объект реализующий автоответчик на приходящие сообщения.
* @author (C) 2003 <a href="mailto:valeks@novel-il.ru">Валентин А. Алексеев</a>
* @version $Id: EchoObject.java,v 1.22 2005/07/22 13:06:57 dron Exp $
*/
public class EchoObject extends StandartODObject {
  private int replyCount = 0;
  /** Регистрация обработчиков. */
  protected final void registerHandlers() {
    addHandler("echo", new MessageHandler() {
      public final void messageReceived(final Message msg) {
        logger.fine("Echo message received");
        final Message m = dispatcher.getNewMessage("echo_reply", msg.getOrigin(), getObjectName(), msg.getId());
        m.addField("replyCount", new Integer(replyCount++));
        m.setCorrect(true);
        dispatcher.send(m);
      }
    });
  }
  /** Выход из объекта.
   * @param type код выхода
   * @return код выхода
   */
  public final int cleanUp(final int type) {
    return 0;
  }

  /** Создание echo-объекта.
   */
  public EchoObject() {
    super("echo", "Echo object", "0.1.0", "(C) 2003-2005 Valentin A. Alekseev");
  }
  /** Получить список предоставляемых сервисов.
   * @return список сервисов
   */
  public final String[] getProviding() {
    final String[] res = {
      "echo"
    };
    return res;
  }
  /** Получить список зависимостей.
   * @return список зависимостей
   */
  public final String[] getDepends() {
    final String[] res = {
      "dispatcher"
    };
    return res;
  }
  
  public Map exportState() {
    Map result = new HashMap();
    result.put("replyCount", new Integer(replyCount));
    return result;
  }
  
  public void importState(Map state) {
    if (state != null) {
      replyCount = ((Integer) state.get("replyCount")).intValue();
      logger.fine("Old replyCount [" + replyCount + "] restored.");
    }
  }
}
