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
package org.valabs.stdobj.webcon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;
import org.valabs.odisp.common.StandartODObject;
import org.valabs.stdmsg.CopyrightGetMessage;
import org.valabs.stdmsg.CopyrightGetReplyMessage;
import org.valabs.stdmsg.ODObjectLoadedMessage;
import org.valabs.stdmsg.webcon.WCAddServletMessage;
import org.valabs.stdmsg.webcon.WCListServletsMessage;
import org.valabs.stdmsg.webcon.WCListServletsReplyMessage;
import org.valabs.stdmsg.webcon.WCRemoveServletMessage;
import org.valabs.stdobj.webcon.servlet.Servlet;
import org.valabs.stdobj.webcon.servlet.ServletException;
import org.valabs.stdobj.webcon.servlet.ServletOutputStream;
import org.valabs.stdobj.webcon.servlet.http.HttpServlet;
import org.valabs.stdobj.webcon.servlet.http.HttpServletRequest;
import org.valabs.stdobj.webcon.servlet.http.HttpServletResponse;

/** ODISP-��������� � ACME �������.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: WebCon.java,v 1.24 2005/11/20 12:33:03 valeks Exp $
 */

public class WebCon extends StandartODObject implements MessageHandler {
  public static final String NAME = "webcon";
  public static final String FULLNAME = "Servlet container interface to ODISP";
  public static final String VERSION = "0.1.0";
  public static final String COPYRIGHT = "(C) 2004 Valentin A. Alekseev";
  /** ������ �� ��� ������. */
  private Serve acmeServe = null;
  /** ����������� ������������ ���������. */
  public void registerHandlers() {
    addHandler(ODObjectLoadedMessage.NAME, this);
    addHandler(WCAddServletMessage.NAME, this);
    addHandler(WCRemoveServletMessage.NAME, this);
    addHandler(WCListServletsMessage.NAME, this);
    addHandler(CopyrightGetMessage.NAME, this);
  }

  /** ���������� ���������� ���������. 
   * @see org.valabs.odisp.common.MessageHandler#messageReceived(org.valabs.odisp.common.Message)
   */
  public final void messageReceived(final Message msg) {
    if (ODObjectLoadedMessage.equals(msg)) {
      if (acmeServe != null) {
        return;
      }
      Thread t = new Thread("WebCon ACME Web Server") {
        public void run() {
          acmeServe = new Serve();
          IndexServlet idx = new IndexServlet();
          acmeServe.addServlet("/", idx);
          acmeServe.addServlet("/index.html", idx);
          acmeServe.serve();
        }
      };
      t.setDaemon(true);
      t.start();
    } else if (WCAddServletMessage.equals(msg)) {
      if (WCAddServletMessage.getServletHandler(msg) instanceof Servlet) {
        // ���������� ������������� �������
        if (acmeServe != null) {
          Servlet s = (Servlet) WCAddServletMessage.getServletHandler(msg);
          acmeServe.addServlet(
            WCAddServletMessage.getServletMask(msg),
            s);
          objectStatus.taskStarted(s.getServletInfo());
        } else {
          logger.warning(
            "attempting to add servlet while container is not started");
        }
      } else {
        logger.warning("handler is not an Servlet extension");
      }
    } else if (WCRemoveServletMessage.equals(msg)) {
      if (WCRemoveServletMessage.getServletHandler(msg) instanceof Servlet) {
        // ���������� ������������� �������
        if (acmeServe != null) {
          Servlet s = (Servlet) WCRemoveServletMessage.getServletHandler(msg);
          acmeServe.removeServlet(
            s);
          objectStatus.taskCompleted(s.getServletInfo());
        } else {
          logger.warning(
            "attempting to add servlet while container is not started");
        }
      } else {
        logger.warning("handler is not an Servlet extension");
      }
    } else if (WCListServletsMessage.equals(msg)) {
      if (acmeServe == null) {
        logger.warning(
          "servlet enumeration request before container was started");
        return;
      }
      Message m = dispatcher.getNewMessage();
      WCListServletsReplyMessage.setup(
        m,
        msg.getOrigin(),
        getObjectName(),
        msg.getId());
      List result = new ArrayList();
      Enumeration e = acmeServe.getServletNames();
      while (e.hasMoreElements()) {
        result.add(e.nextElement());
      }
      WCListServletsReplyMessage.setServletsList(m, result);
      dispatcher.send(m);
    } else if (CopyrightGetMessage.equals(msg)) {
    	List result = new ArrayList();
    	result.add("ACME Web Server portions copyright: (C)1996,1998 by Jef Poskanzer <jef@acme.com>. (C) 2004-2005 Valentin A. Alekseev <valeks@valabs.spb.ru>");
    	Message m = dispatcher.getNewMessage();
    	CopyrightGetReplyMessage.setup(m, msg.getOrigin(), getObjectName(), msg.getId());
    	CopyrightGetReplyMessage.setCopyrights(m, result);
    	dispatcher.send(m);
    }
  }

  /** ����������� �������.
   */
  public WebCon() {
    super(NAME, FULLNAME, VERSION, COPYRIGHT);
  }

  /** ������� ������ ��������. 
   * @see org.valabs.odisp.common.ODObject#getProviding()
   */
  public String[] getProviding() {
    String[] result = { "webcon", };
    return result;
  }

  /** ������� ������ ������������. 
   * @see org.valabs.odisp.common.ODObject#getDepends()
   */
  public String[] getDepends() {
    String[] result = { "dispatcher", };
    return result;
  }

  /** ���������� ������.
   * @param exitCode ��� ����������
   * @see org.valabs.odisp.common.ODObject#cleanUp(int)
   */
  public int cleanUp(int exitCode) {
    return exitCode;
  }

  /** ���������� index ��������. */
  private class IndexServlet extends HttpServlet {
    /** ���������� � ��������. 
     * @see org.valabs.stdobj.webcon.servlet.Servlet#getServletInfo()
     */
    public String getServletInfo() {
      return "IndexServlet: servlet that act as front page for WebCon";
    }
    
    /**
     * @see org.valabs.stdobj.webcon.servlet.http.HttpServlet#service(org.valabs.stdobj.webcon.servlet.http.HttpServletRequest, org.valabs.stdobj.webcon.servlet.http.HttpServletResponse)
     */
    public void service(
      final HttpServletRequest req,
      final HttpServletResponse res)
      throws ServletException, IOException {
      res.setStatus(HttpServletResponse.SC_OK);
      res.setContentType("text/xhtml");
      ServletOutputStream p = res.getOutputStream();
      p.println(
        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
      p.println("<html>");
      p.println("\t<head>");
      p.println("\t\t<title>ODISP WebCon default index page</title>");
      p.println("\t\t<meta http-equiv='Content-type' content='text/html'/>");
      p.println("\t</head>");
      p.println("\t<body>");
      p.println("\t\t<h3>ODISP WebCon default index page</h3>");
      // ������ ������������������ ���������
      Enumeration e = acmeServe.getServlets();
      if (e.hasMoreElements()) {
        p.println("\t\t<hr/>");
        p.println("\t\t<p>List of known servlets");
        p.println("\t\t<ul>");
        while (e.hasMoreElements()) {
          p.println(
            "\t\t\t<li>"
              + ((Servlet) e.nextElement()).getServletInfo()
              + "</li>");
        }
        p.println("\t\t</ul>");
        p.println("\t\t</p>");
      }
      p.println("\t</body>");
      p.println("</html>");
      p.flush();
      p.close();
    }
  }
} // WebCon
