package org.valabs.stdobj.copyright;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.doomdark.uuid.UUID;
import org.valabs.odisp.SessionManager;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;
import org.valabs.odisp.common.StandartODObject;
import org.valabs.stdmsg.CopyrightGetMessage;
import org.valabs.stdmsg.CopyrightGetReplyMessage;

/** ��������� ������, ������� ���������� ������ ���������� � ������
 * �� �� ������������ � ������� ODISP.
 * @author <a href="mailto:valeks@valabs.spb.ru">�������� �������� �.</a>
 * @version $Id: Copyright.java,v 1.1 2005/07/18 08:36:15 valeks Exp $
 */
public class Copyright extends StandartODObject implements MessageHandler {
	private static final String NAME = "copyright";
	private static final String FULLNAME = "Copyright Information Module";
	private static final String VERSION = "0.1.0";
	private static final String COPYRIGHT = "(C) 2005 Valentin A. Alekseev";
	
	public void registerHandlers() {
		addHandler(CopyrightGetMessage.NAME, this);
	}
	
	public Copyright() {
		super(NAME, FULLNAME, VERSION, COPYRIGHT);
	}

	public String[] getDepends() {
		return new String[] { "dispatcher", };
	}

	public String[] getProviding() {
		return new String[] { NAME, };
	}

	public void messageReceived(Message msg) {
		if (CopyrightGetMessage.equals(msg) && !msg.getOrigin().equals(getObjectName())) {
			new CopyrightGetTask(msg.getOrigin(), msg.getId());
		}
	}
	
	private class CopyrightGetTask implements MessageHandler {
		private String origin;
		private UUID msgId;
		private JFrame copyrightFrame;
		private CopyrightTableModel ctm = new CopyrightTableModel();
		public CopyrightGetTask(final String _origin, final UUID _msgId) {
			origin = _origin;
			msgId = _msgId;
			Message m = getDispatcher().getNewMessage();
			CopyrightGetMessage.setup(m, Message.RECIPIENT_ALL, getObjectName(), UUID.getNullUUID());
			SessionManager.getSessionManager().addMessageListener(m.getId(), this, true);
			if (getParameter("ui", "cli").equals("gui")) {
				copyrightFrame = new JFrame("Copyright Information");
				copyrightFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				copyrightFrame.getContentPane().add(new JScrollPane(new JTable(ctm)));
				copyrightFrame.pack();
				copyrightFrame.setVisible(true);
			}
			getDispatcher().send(m);
		}

		public void messageReceived(Message msg) {
			if (getParameter("ui", "cli").equals("cli")) {
				messageReceivedCLI(msg);
			} else {
				messageReceivedGUI(msg);
			}
		}

		private void messageReceivedCLI(Message msg) {
			if (CopyrightGetReplyMessage.equals(msg)) {
				List results = CopyrightGetReplyMessage.getCopyrights(msg);
				System.out.println("Copyright information for object: " + msg.getOrigin());
				Iterator it = results.iterator();
				int i = 0;
				while (it.hasNext()) {
					String element = (String) it.next();
					System.out.println((i++) + ":" + element);
				}
			}
		}

		private void messageReceivedGUI(Message msg) {
			if (CopyrightGetReplyMessage.equals(msg)) {
				List results = CopyrightGetReplyMessage.getCopyrights(msg);
				ctm.addModuleCopyrightInfo(msg.getOrigin(), results);
			}
		}

		private class CopyrightTableModel extends AbstractTableModel {
			private Map values = new HashMap();
			private int idx = 0;
			
			public String getColumnName(int column) {
				String result = null;
				switch (column) {
					case 0:
						result = "Module";
						break;
					default:
					case 1:
						result = "Copyright Info";
				}
				return result;
			}
			public void addModuleCopyrightInfo(String moduleName, List copyrightInfo) {
				Iterator it = copyrightInfo.iterator();
				while (it.hasNext()) {
					String copyInfo = (String) it.next();
					values.put(moduleName + "-" + (idx++), copyInfo);
				}
				fireTableDataChanged();
			}
			
			public int getRowCount() {
				return values.size();
			}

			public int getColumnCount() {
				return 2;
			}

			public Object getValueAt(int rowIndex, int columnIndex) {
				Iterator it = values.entrySet().iterator();
				int rowIdx = 0;
				while (it.hasNext()) {
					Map.Entry entry = (Map.Entry) it.next();
					if (rowIdx == rowIndex) {
						if (columnIndex == 0) {
							String modulename = (String) entry.getKey();
							return modulename.substring(0, modulename.lastIndexOf('-')); 
						} else {
							return entry.getValue();
						}
					}
					rowIdx++;
				}
				return null;
			}
		}
	} // CopyrightGetTask
} // Copyright