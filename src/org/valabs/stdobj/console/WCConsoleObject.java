package org.valabs.stdobj.console;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.doomdark.uuid.UUID;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.StandartODObject;
import org.valabs.stdmsg.ODObjectLoadedMessage;
import org.valabs.stdmsg.webcon.WCAddServletMessage;
import org.valabs.stdobj.webcon.servlet.ServletException;
import org.valabs.stdobj.webcon.servlet.ServletOutputStream;
import org.valabs.stdobj.webcon.servlet.http.HttpServlet;
import org.valabs.stdobj.webcon.servlet.http.HttpServletRequest;
import org.valabs.stdobj.webcon.servlet.http.HttpServletResponse;

/** ������ ODISP ����������� WebCon ��������� ������� � ���������.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">�������� �. ��������</a>
 * @version $Id: WCConsoleObject.java,v 1.17 2005/07/22 13:06:53 dron Exp $
 */
public class WCConsoleObject extends StandartODObject {
  public static final String NAME = "wcconsole";
  public static final String FULLNAME = "ODISP Web Console";
  public static final String VERSION = "0.1.0";
  public static final String COPYRIGHT = "(C) 2004 Valentin A. Alekseev";
  
  /** ���������� �������-����������. */
  private final WCConsoleServlet servlet = new WCConsoleServlet();
  /** ���������� �������� ���������.
   * @param msg ���������
   */
  public final void handleMessage(final Message msg) {
    if (ODObjectLoadedMessage.equals(msg)) {
      final Message m = dispatcher.getNewMessage();
      WCAddServletMessage.setup(m, "webcon", getObjectName(), UUID.getNullUUID());
      WCAddServletMessage.setServletMask(m, "/wcconsole");
      WCAddServletMessage.setServletHandler(m, servlet);
      dispatcher.send(m);
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
    return 0;
  }
  /** ����������� ������� � �������� ���������� �������.
   */
  public WCConsoleObject() {
    super(NAME, FULLNAME, VERSION, COPYRIGHT);
  }
  /** ������� ������ ��������.
   * @return ������ ��������
   */
  public final String[] getProviding() {
    final String[] res = {NAME};
    return res;
  }
  /** ������� ������ ������������.
   * @return ������ ������������
   */
  public final String[] getDepends() {
    final String[] res = {
      "dispatcher",
      "webcon",
    };
    return res;
  }

  private class WCConsoleServlet extends HttpServlet {
    private final List messages = new ArrayList();
    /** ���������� � ��������. */
    public String getServletInfo() {
      return "WCConsoleServlet: servlet that act as ODISP console. <a href=\"/wcconsole\">Start servlet</a>";
    }

    /** ���������� �������. */
    public void service(final HttpServletRequest req, final HttpServletResponse res) throws ServletException,
            IOException {
      res.setStatus(HttpServletResponse.SC_OK);
      res.setContentType("text/xhtml");
      final ServletOutputStream page = res.getOutputStream();
      page.println("<html>");
      page.println("\t<head>");
      page.println("\t\t<title>ODISP WebCon console</title>");
      if (req.getQueryString() == null) {
        // ���������� ������� ��������� ��������
        page.println("\t</head>");
        page.println("\t<frameset rows=\"80%, *\">");
        page.println("\t\t<frame src=\"/wcconsole?log\"/>");
        page.println("\t\t<frame src=\"/wcconsole?input\"/>");
        page.println("\t</frameset>");
        page.println("\t<noframes>");
        page.println("\t\t<h3>Non-framed version of WebCon's ODISP console is not available</h3>");
        page.println("\t</noframes>");
      } else if (req.getQueryString().startsWith("log")) {
        page.println("\t\t<meta http-equiv=\"Refresh\" content=\"30\"/>");
        page.println("\t</head>");
        page.println("\t<body>");
        page.println("\t\t<table border=\"0\">");
        page.println("\t\t\t<tr><td><a href=\"?log\">refresh</a></td></tr>");
        List localMessages = null;
        synchronized (messages) {
          localMessages = new ArrayList(messages);
        }
        if (localMessages != null && localMessages.size() > 0) {
          Iterator it = localMessages.iterator();
          while (it.hasNext()) {
            Message msg = (Message) it.next();
            // ���� ������ �������� ��������� toString(true)
            page.println("\t\t\t<tr><td><pre>");
            page.println(msg.toString(true));
            page.println("\t\t\t</pre></td></tr>");
          }
        } else {
          page.println("\t\t\t<tr><td>No messages</td></tr>");
        }
        page.println("\t\t</table>");
        page.println("\t</body>");
      } else if (req.getQueryString().startsWith("input")) {
        if (req.getParameter("go") != null) {
          String objectName = req.getParameter("object");
          String message = req.getParameter("message");
          objectName = objectName.replaceAll("%2A", "*");
          Message m = dispatcher.getNewMessage(message, objectName, getObjectName(), UUID.getNullUUID());
          m.setCorrect(true);
          dispatcher.send(m);
        }
        page.println("\t</head>");
        page.println("\t<body>");
        page.println("\t\t<form>");
        page.println("\t\t<input type='hidden' name='input'/>");
        page.println("\t\t<input type='hidden' name='go'/>");
        page.println("\t\t\t<table border='0'>");
        page.println("\t\t\t<tr><td>Message:</td><td><input type='text' name='message'/></td>");
        page.println("\t\t\t<tr><td>Object:</td><td><input type='text' name='object'/></td>");
        page.println("\t\t\t<tr><td colspan='2'><input type='submit'/></td>");
        page.println("\t\t\t</table>");
        page.println("\t\t</form>");
        page.println("\t</body>");
      }
      page.println("</html>");
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
