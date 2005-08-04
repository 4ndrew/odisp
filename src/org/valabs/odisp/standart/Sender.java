package org.valabs.odisp.standart;


/** Реализация единого потока рассылки и обработки сообщений.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: Sender.java,v 1.15 2005/08/04 10:50:45 valeks Exp $
 */
class Sender extends Thread {
	/** Счетчик сообщений, которые были обработаны нитью. */
	private int messageCounter = 0;
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
				System.err.println("Sender stats: messageCount=" + messageCounter);
			}
		});
		
		setDaemon(true);
		oman = noman;
		start();
	}

	/** Основной метод нитки, который отсылает сообщений. */
	public final void run() {
		while (!doExit) {
			try {
				synchronized (this) {
					wait(100);
				}
			} catch (InterruptedException e) {
				doExit = true;
			}
			SendRecord toSend = null; 
			synchronized (oman) {
				toSend = oman.getNextPendingMessage();
			}
			if (toSend != null) {
				try {
					toSend.getObject().handleMessage0(toSend.getMessage());
					messageCounter++;
				} catch (Exception e) {
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
