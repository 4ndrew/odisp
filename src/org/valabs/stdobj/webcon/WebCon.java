package com.novel.stdobj.webcon;

import com.novel.odisp.common.CallbackODObject;
import com.novel.odisp.common.Message;
import com.novel.odisp.common.MessageHandler;
import com.novel.stdmsg.ODObjectLoadedMessage;
import com.novel.stdmsg.ODCleanupMessage;
import com.novel.stdmsg.webcon.WCAddServletMessage;
import com.novel.stdmsg.webcon.WCRemoveServletMessage;
import com.novel.stdmsg.webcon.WCListServletsMessage;
import com.novel.stdmsg.webcon.WCListServletsReplyMessage;
import com.novel.stdobj.webcon.servlet.Servlet;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;

// для IndexServlet
import com.novel.stdobj.webcon.servlet.http.HttpServlet;
import com.novel.stdobj.webcon.servlet.http.HttpServletRequest;
import com.novel.stdobj.webcon.servlet.http.HttpServletResponse;
import com.novel.stdobj.webcon.servlet.ServletException;
import com.novel.stdobj.webcon.servlet.ServletOutputStream;
import java.io.IOException;

/** ODISP-интерфейс к ACME серверу.
 * @author <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @author (C) 2004, НПП "Новел-ИЛ"
 * @version $Id: WebCon.java,v 1.2 2004/03/27 23:20:59 valeks Exp $
 */

public class WebCon extends CallbackODObject implements MessageHandler {
  /** Ссылка на сам сервер. */
  private Serve acmeServe = null;
  /** Регистрация обработчиков сообщений. */
  public void registerHandlers() {
    addHandler(ODObjectLoadedMessage.NAME, this);
    addHandler(ODCleanupMessage.NAME, this);
    addHandler(WCAddServletMessage.NAME, this);
    addHandler(WCRemoveServletMessage.NAME, this);
    addHandler(WCListServletsMessage.NAME, this);
  }

  /** Обработчик приходящих сообщений. */
  public final void messageReceived(final Message msg) {
    if (msg instanceof ODObjectLoadedMessage) {
      if (acmeServe != null) {
	return;
      }
      acmeServe = new Serve();
      IndexServlet idx = new IndexServlet();
      acmeServe.addServlet("/", idx);
      acmeServe.addServlet("/index.html", idx);
      acmeServe.serve();
    } else if (msg instanceof WCAddServletMessage) {
      WCAddServletMessage m = (WCAddServletMessage) msg;
      if (m.getServletHandler() instanceof Servlet) {
	// обработчик действительно сервлет
	if (acmeServe != null) {
	  acmeServe.addServlet(m.getServletMask(), (Servlet) m.getServletHandler());
	} else {
	  logger.warning("attempting to add servlet while container is not started");
	}
      } else {
	logger.warning("handler is not an Servlet extension");
      }
    } else if (msg instanceof WCRemoveServletMessage) {
      WCRemoveServletMessage m = (WCRemoveServletMessage) msg;
      if (m.getServletHandler() instanceof Servlet) {
	// обработчик действительно сервлет
	if (acmeServe != null) {
	  acmeServe.removeServlet((Servlet) m.getServletHandler());
	} else {
	  logger.warning("attempting to add servlet while container is not started");
	}
      } else {
	logger.warning("handler is not an Servlet extension");
      }
    } else if (msg instanceof WCListServletsMessage) {
      if (acmeServe == null) {
	logger.warning("servlet enumeration request before container was started");
	return;
      }
      WCListServletsReplyMessage m = new WCListServletsReplyMessage(msg.getOrigin(), getObjectName(), msg.getId());
      List result = new ArrayList();
      Enumeration e = acmeServe.getServletNames();
      while (e.hasMoreElements()) {
	result.add(e.nextElement());
      }
      m.setServletsList(result);
      dispatcher.send(m);
    } else if (msg instanceof ODCleanupMessage) {
      cleanUp(0);
    }
  }

  /** Конструктор объекта.
   * @param id порядковый номер
   */
  public WebCon(Integer id) {
    super("webcon" + id);
  }

  /** Вернуть список сервисов. */
  public String[] getProviding() {
    String[] result = {
      "webcon",
    };
    return result;
  }

  /** Вернуть список зависимостей. */
  public String[] getDepends() {
    String[] result = {
      "stddispatcher",
    };
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
    public void service(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
      res.setStatus(HttpServletResponse.SC_OK);
      res.setContentType("text/xhtml");
      ServletOutputStream p = res.getOutputStream();
      p.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
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
	  p.println("\t\t\t<li>" + ((Servlet) e.nextElement()).getServletInfo() + "</li>");
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
}// WebCon
