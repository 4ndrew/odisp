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
package org.valabs.odisp.standart;

/** Реализация единого потока рассылки и обработки сообщений.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: Sender.java,v 1.17 2006/06/26 16:30:21 dron Exp $
 */
class Sender extends Thread {
	/** Счетчик сообщений, которые были обработаны нитью. */
	private int messageCounter = 0;
    /** Счетчик ошибок при доставке сообщения. */
    private int exceptionCounter = 0;
	/** Счетчик потоков. */
	private static int counter = 0;
	/** Условие окончания обработки. */
	private boolean doExit = false;
	/** Ссылка на диспетчер объектов. */
	private final ObjectManager oman;
	/** Стандартный конструктор. 
	 * @param noman ссылка на диспетчер объектов 
	 */
	public Sender(ObjectManager noman) {
		super("sender" + (counter++));
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.err.println("Sender stats: messageCount=" + messageCounter + ", exceptionCounter=" + exceptionCounter);
			}
		});
		
		setDaemon(true);
		oman = noman;
		start();
	}

	/** Основной метод нитки, который отсылает сообщений. */
	public final void run() {
        SendRecord toSend = null;
		while (!doExit) {    
			try {
                if (toSend == null) {
                  synchronized (this) {
                    wait(100);
                  }
                }
			} catch (InterruptedException e) {
				doExit = true;
			}
			 
			synchronized (oman) {
				toSend = oman.getNextPendingMessage();
			}
			if (toSend != null) {
				try {
					toSend.getObject().handleMessage0(toSend.getMessage());
					messageCounter++;
				} catch (Exception e) {
                    exceptionCounter++;
					oman.signalException(e);
				}
			}
		}
	}

	/** Принудительное завершение работы нити. */
	public void quit() {
		doExit = true;
		synchronized (this) {
			notify();
		}
	}

	/** Доступ к счетчику сообщений. 
	 * @return количество сообщений обработаных нитью 
	 */
	public int getCounter() {
		return messageCounter;
	}
} // Sender
