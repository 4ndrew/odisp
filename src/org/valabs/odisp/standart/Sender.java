package com.novel.odisp.standart;


/** ���������� ������� ������ �������� � ��������� ���������.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: Sender.java,v 1.10 2004/07/21 08:05:42 valeks Exp $
 */
class Sender extends Thread {
	/** ������� ���������, ������� ���� ���������� �����. */
	private int messageCounter = 0;
	/** ������� �������. */
	private static int counter = 0;
	/** ������� ��������� ���������. */
	private boolean doExit = false;
	/** ������ �� ��������� ��������. */
	private ObjectManager oman = null;
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
	public void run() {
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
					toSend.getObject().handleMessage(toSend.getMessage());
					messageCounter++;
					//System.err.println("Sender [" + getName() + "] message processed: " + toSend);
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
