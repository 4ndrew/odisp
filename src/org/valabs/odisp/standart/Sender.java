package com.novel.odisp;


/** Реализация единого потока рассылки и обработки сообщений.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: Sender.java,v 1.2 2004/05/14 07:56:42 valeks Exp $
 */
public class Sender extends Thread {
	/** Счетчик сообщений, которые были обработаны нитью. */
	private int messageCounter = 0;
	/** Счетчик потоков. */
	private static int counter = 0;
	/** Условие окончания обработки. */
	private boolean doExit = false;
	/** Ссылка на диспетчер объектов. */
	private StandartObjectManager oman = null;
	/** Стандартный конструктор. 
	 * @param oman ссылка на диспетчер объектов 
	 */
	public Sender(StandartObjectManager noman) {
		super("sender" + (counter++));
		setDaemon(true);
		oman = noman;
		start();
	}

	/** Основной метод нитки, который отсылает сообщений. */
	public void run() {
		while (!doExit) {
			try {
				synchronized (this) {
					wait(1000);
				}
			} catch (InterruptedException e) {
				doExit = true;
			}
			SendRecord toSend = null; 
			synchronized (oman) {
				toSend = oman.getNextPendingMessage();
			}
			if (toSend != null) {
				toSend.getObject().addMessage(toSend.getMessage());
				messageCounter++;
				System.err.println("Sender [" + getName() + "] message processed: " + toSend);
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

	/** Доступ к счетчику сообщений в очереди. */
	public int getCounter() {
		return messageCounter;
	}
} // Sender
