package org.valabs.stdobj.echo;

import java.util.HashMap;
import java.util.Map;

import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;
import org.valabs.odisp.common.StandartODObject;

/** Простейший ODISP объект реализующий автоответчик на приходящие сообщения.
* @author (C) 2003 <a href="mailto:valeks@novel-il.ru">Валентин А. Алексеев</a>
* @version $Id: EchoObject.java,v 1.19 2005/01/26 08:22:54 valeks Exp $
*/
public class EchoObject extends StandartODObject {
  private int replyCount = 0;
  /** Регистрация обработчиков. */
  protected final void registerHandlers() {
    addHandler("echo", new MessageHandler() {
	public final void messageReceived(final Message msg) {
	  Message m
	    = dispatcher.getNewMessage("echo_reply", msg.getOrigin(), getObjectName(), msg.getId());
	  m.addField("replyCount", new Integer(replyCount));
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
   * @param id индекс объекта
   */
  public EchoObject() {
    super("echo", "Echo object", "0.1.0", "(C) 2003-2005 Valentin A. Alekseev");
  }
  /** Получить список предоставляемых сервисов.
   * @return список сервисов
   */
  public final String[] getProviding() {
    String[] res = {
      "echo"
    };
    return res;
  }
  /** Получить список зависимостей.
   * @return список зависимостей
   */
  public final String[] getDepends() {
    String[] res = {
      "dispatcher",
      "log"
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
    }
  }
}
