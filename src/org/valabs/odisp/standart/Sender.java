package org.valabs.odisp.standart;


/** ���������� ������� ������ �������� � ��������� ���������.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: Sender.java,v 1.15 2005/08/04 10:50:45 valeks Exp $
 */
class Sender extends Thread {
	/** ������� ���������, ������� ���� ���������� �����. */
	private int messageCounter = 0;
	/** ������� �������. */
	private static int counter = 0;
	/** ������� ��������� ���������. */
	private boolean doExit = false;
	/** ������ �� ��������� ��������. */
	private final ObjectManager oman;
	/** ����������� �����������. 
	 * @param noman ������ �� ��������� �������� 
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

	/** �������� ����� �����, ������� �������� ���������. */
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

	/** �������������� ���������� ������ ����. */
	public void quit() {
		doExit = true;
		synchronized (this) {
			notify();
		}
	}

	/** ������ � �������� ���������. 
	 * @return ���������� ��������� ����������� ����� 
	 */
	public int getCounter() {
		return messageCounter;
	}
} // Sender
