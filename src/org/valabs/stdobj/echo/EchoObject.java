package com.novel.stdobj.echo;

import com.novel.odisp.common.CallbackODObject;
import com.novel.odisp.common.Message;
import com.novel.odisp.common.MessageHandler;

/** Простейший ODISP объект реализующий автоответчик на приходящие сообщения.
* @author Валентин А. Алексеев
* @author (C) 2003, НПП "Новел-ИЛ"
* @version $Id: EchoObject.java,v 1.11 2004/03/17 11:46:36 dron Exp $
*/
public class EchoObject extends CallbackODObject {
  /** Регистрация обработчиков. */
  protected final void registerHandlers() {
    addHandler("od_cleanup", new MessageHandler() {
	public final void messageReceived(final Message msg) {
	  cleanUp(((Integer) msg.getField("0")).intValue());
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
    super("echo" + id);
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
      "stddispatcher",
      "log"
    };
    return res;
  }
}
