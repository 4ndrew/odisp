package com.novel.stdmsg;

import com.novel.odisp.*;
import com.novel.odisp.common.*;

/** Класс реализующий сообщение о загрузке объекта.
 * Данное сообщение не имеет ни каких дополнительных полей, по этому оно не расширяет
 * свой базовый класс StandartMessage.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: ODObjectLoadedMessage.java,v 1.1 2003/12/01 22:19:29 valeks Exp $
 */

public class ODObjectLoadedMessage extends StandartMessage {
  public ODObjectLoadedMessage(String destination, String origin, int replyTo) {
    super("od_object_loaded", destination, origin, replyTo);
  }
}// ODObjectLoaded
