package org.valabs.stdobj.echo;

import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;
import org.valabs.odisp.common.StandartODObject;
import org.valabs.stdmsg.ODCleanupMessage;

/** Простейший ODISP объект реализующий автоответчик на приходящие сообщения.
* @author (C) 2003 <a href="mailto:valeks@novel-il.ru">Валентин А. Алексеев</a>
* @version $Id: EchoObject.java,v 1.18 2005/01/25 19:03:34 valeks Exp $
*/
public class EchoObject extends StandartODObject {
  /** Регистрация обработчиков. */
  protected final void registerHandlers() {
    addHandler(ODCleanupMessage.NAME, new MessageHandler() {
	public final void messageReceived(final Message msg) {
	  cleanUp(ODCleanupMessage.getReason(msg).intValue());
	}
      });
    addHandler("echo", new MessageHandler() {
	public final void messageReceived(final Message msg) {
	  Message m
	    = dispatcher.getNewMessage("echo_reply", msg.getOrigin(), getObjectName(), msg.getId());
	  for (int i = 0; i < msg.getFieldsCount(); i++) {
	    m.addField("" + i, msg.getField("" + i));
	  }
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
  public EchoObject(final Integer id) {
    super("echo" + id, "Echo object", "0.1.0", "(C) 2003-2005 Valentin A. Alekseev");
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
}
