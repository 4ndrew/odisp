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

/** ���������� ������� ������ �������� � ��������� ���������.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: Sender.java,v 1.17 2006/06/26 16:30:21 dron Exp $
 */
class Sender extends Thread {
	/** ������� ���������, ������� ���� ���������� �����. */
	private int messageCounter = 0;
    /** ������� ������ ��� �������� ���������. */
    private int exceptionCounter = 0;
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
				System.err.println("Sender stats: messageCount=" + messageCounter + ", exceptionCounter=" + exceptionCounter);
			}
		});
		
		setDaemon(true);
		oman = noman;
		start();
	}

	/** �������� ����� �����, ������� �������� ���������. */
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
