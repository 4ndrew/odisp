package org.valabs.odisp.standart;


/** ���������� ������� ������ �������� � ��������� ���������.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: Sender.java,v 1.13 2005/02/27 12:37:31 valeks Exp $
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

	/** ������ � �������� ��������� � �������. */
	public int getCounter() {
		return messageCounter;
	}
} // Sender
