package org.valabs.stdobj.console;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.doomdark.uuid.UUID;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.StandartODObject;
import org.valabs.stdmsg.ODCleanupMessage;
import org.valabs.stdmsg.ODObjectLoadedMessage;
import org.valabs.stdmsg.webcon.WCAddServletMessage;
import org.valabs.stdobj.webcon.servlet.ServletException;
import org.valabs.stdobj.webcon.servlet.ServletOutputStream;
import org.valabs.stdobj.webcon.servlet.http.HttpServlet;
import org.valabs.stdobj.webcon.servlet.http.HttpServletRequest;
import org.valabs.stdobj.webcon.servlet.http.HttpServletResponse;

/** ������ ODISP ����������� WebCon ��������� ������� � ���������.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
 * @version $Id: WCConsoleObject.java,v 1.13 2005/01/25 19:03:34 valeks Exp $
 */
public class WCConsoleObject extends StandartODObject {
  public static final String NAME = "wcconsole";
  public static final String FULLNAME = "ODISP Web Console";
  public static final String VERSION = "0.1.0";
  public static final String COPYRIGHT = "(C) 2004 Valentin A. Alekseev";
  
  /** ���������� �������-����������. */
  WCConsoleServlet servlet = null;
  /** ���������� �������� ���������.
   * @param msg ���������
   */
  public final void handleMessage(final Message msg) {
    if (ODObjectLoadedMessage.equals(msg)) {
      servlet = new WCConsoleServlet();
      Message m = dispatcher.getNewMessage();
      WCAddServletMessage.setup(m, "webcon", getObjectName(), UUID.getNullUUID());
      WCAddServletMessage.setServletMask(m, "/wcconsole");
      WCAddServletMessage.setServletHandler(m, servlet);
      dispatcher.send(m);
    } else if (ODCleanupMessage.equals(msg)) {
      cleanUp(ODCleanupMessage.getReason(msg).intValue());
    } else {
      servlet.messageReceived(msg);
    }
    return;
  }
  /** �����.
   * @param type ������� ������
   * @return ��� ��������
   */
  public final int cleanUp(final int type) {
    logger.finest("Cleaning up...");
    return 0;
  }
  /** ����������� ������� � �������� ���������� �������.
   * @param id �����
   */
  public WCConsoleObject(final Integer id) {
    super(NAME + id, FULLNAME, VERSION, COPYRIGHT);
  }
  /** ������� ������ ��������.
   * @return ������ ��������
   */
  public final String[] getProviding() {
    String[] res = {NAME};
    return res;
  }
  /** ������� ������ ������������.
   * @return ������ ������������
   */
  public final String[] getDepends() {
    String[] res = {
      "dispatcher",
      "webcon",
    };
    return res;
  }

  private class WCConsoleServlet extends HttpServlet {
    private List messages = new ArrayList();
    /** ���������� � ��������. */
    public String getServletInfo() {
      return "WCConsoleServlet: servlet that act as ODISP console. <a href=\"/wcconsole\">Start servlet</a>";
    }

    /** ���������� �������. */
    public void service(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
      res.setStatus(HttpServletResponse.SC_OK);
      res.setContentType("text/xhtml");
      ServletOutputStream p = res.getOutputStream();
      p.println("<html>");
      p.println("\t<head>");
      p.println("\t\t<title>ODISP WebCon console</title>");
      if (req.getQueryString() == null) {
	// ���������� ������� ��������� ��������
	p.println("\t</head>");
	p.println("\t<frameset rows=\"80%, *\">");
	p.println("\t\t<frame src=\"/wcconsole?log\"/>");
	p.println("\t\t<frame src=\"/wcconsole?input\"/>");
	p.println("\t</frameset>");
	p.println("\t<noframes>");
	p.println("\t\t<h3>Non-framed version of WebCon's ODISP console is not available</h3>");
	p.println("\t</noframes>");
      } else if (req.getQueryString().startsWith("log")) {
	p.println("\t\t<meta http-equiv=\"Refresh\" content=\"30\"/>");
	p.println("\t</head>");
	p.println("\t<body>");
	p.println("\t\t<table border=\"0\">");
	  p.println("\t\t\t<tr><td><a href=\"?log\">refresh</a></td></tr>");
	List localMessages = null;
	synchronized (messages) {
	  localMessages = new ArrayList(messages);
	}
	if (localMessages != null && localMessages.size() > 0) {
	  Iterator it = localMessages.iterator();
	  while (it.hasNext()) {
	    Message msg = (Message) it.next();
	    // ���� ������ �������� ��������� toString(true)
	    p.println("\t\t\t<tr><td><pre>");
	    p.println(msg.toString(true));
	    p.println("\t\t\t</pre></td></tr>");
	  }
	} else {
	  p.println("\t\t\t<tr><td>No messages</td></tr>");
	}
	p.println("\t\t</table>");
	p.println("\t</body>");
      } else if (req.getQueryString().startsWith("input")) {
	if (req.getParameter("go") != null) {
	  String objectName = req.getParameter("object");
	  String message = req.getParameter("message");
	  objectName = objectName.replaceAll("%2A", "*");
	  Message m = dispatcher.getNewMessage(message, objectName, getObjectName(), UUID.getNullUUID());
	  dispatcher.send(m);
	}
	p.println("\t</head>");
	p.println("\t<body>");
	p.println("\t\t<form>");
	p.println("\t\t<input type='hidden' name='input'/>");
	p.println("\t\t<input type='hidden' name='go'/>");
	p.println("\t\t\t<table border='0'>");
	p.println("\t\t\t<tr><td>Message:</td><td><input type='text' name='message'/></td>");
	p.println("\t\t\t<tr><td>Object:</td><td><input type='text' name='object'/></td>");
	p.println("\t\t\t<tr><td colspan='2'><input type='submit'/></td>");
	p.println("\t\t\t</table>");
	p.println("\t\t</form>");
	p.println("\t</body>");
      }
      p.println("</html>");
    }

    /** ���������� ��������� � ������. */
    public synchronized void messageReceived(final Message msg) {
      if (messages.size() == 30) {
	// �������� ���������� ���������
	messages.remove(0);
      }
      messages.add(msg);
    }
  } // WCConsoleServlet
} // WCConsoleObject
