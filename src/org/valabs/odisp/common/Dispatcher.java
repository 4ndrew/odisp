package com.novel.odisp.common;

/** Стандартный интерфейс диспетчера, который должен реализовывать
 * класс, который выступает в качестве ODISP диспетчера.
 * @author Валентин А. Алексеев
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: Dispatcher.java,v 1.2 2003/10/03 21:22:11 valeks Exp $
 */
public interface Dispatcher {
	public void sendMessage(Message message);
	public Message getNewMessage(String action, String destination, String origin, int inReplyTo);
}