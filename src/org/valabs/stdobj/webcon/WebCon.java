package org.valabs.stdobj.webcon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.MessageHandler;
import org.valabs.odisp.common.StandartODObject;
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

/** ODISP-интерфейс к ACME серверу.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: WebCon.java,v 1.17 2005/01/26 09:12:14 valeks Exp $
 */

public class WebCon extends StandartODObject implements MessageHandler {
  public static final String NAME = "webcon";
  public static final String FULLNAME = "Servlet container interface to ODISP";
  public static final String VERSION = "0.1.0";
  public static final String COPYRIGHT = "(C) 2004 Valentin A. Alekseev";
  /** Ссылка на сам сервер. */
  private Serve acmeServe = null;
  /** Регистрация обработчиков сообщений. */
  public void registerHandlers() {
    addHandler(ODObjectLoadedMessage.NAME, this);
    addHandler(WCAddServletMessage.NAME, this);
    addHandler(WCRemoveServletMessage.NAME, this);
    addHandler(WCListServletsMessage.NAME, this);
  }

  /** Обработчик приходящих сообщений. */
  public final void messageReceived(final Message msg) {
    if (ODObjectLoadedMessage.equals(msg)) {
      if (acmeServe != null) {
        return;
      }
      Thread t = new Thread() {
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
        // обработчик действительно сервлет
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
        // обработчик действительно сервлет
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
    }
  }

  /** Конструктор объекта.
   * @param id порядковый номер
   */
  public WebCon() {
    super(NAME, FULLNAME, VERSION, COPYRIGHT);
  }

  /** Вернуть список сервисов. */
  public String[] getProviding() {
    String[] result = { "webcon", };
    return result;
  }

  /** Вернуть список зависимостей. */
  public String[] getDepends() {
    String[] result = { "dispatcher", };
    return result;
  }

  /** Завершение работы.
   * @param exitCode код завершения
   */
  public int cleanUp(int exitCode) {
    return exitCode;
  }

  /** Обработчик index запросов. */
  private class IndexServlet extends HttpServlet {
    /** Информация о сервлете. */
    public String getServletInfo() {
      return "IndexServlet: servlet that act as front page for WebCon";
    }
    /** Выполнение запроса. */
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
      // список зарегистрированных сервлетов
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
