package com.novel.stdobj.console;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.novel.odisp.common.Message;
import com.novel.odisp.common.StandartODObject;
import com.novel.stdmsg.ODCleanupMessage;
import com.novel.stdmsg.ODObjectLoadedMessage;
import com.novel.stdmsg.webcon.WCAddServletMessage;
import com.novel.stdobj.webcon.servlet.ServletException;
import com.novel.stdobj.webcon.servlet.ServletOutputStream;
import com.novel.stdobj.webcon.servlet.http.HttpServlet;
import com.novel.stdobj.webcon.servlet.http.HttpServletRequest;
import com.novel.stdobj.webcon.servlet.http.HttpServletResponse;

/** Объект ODISP реализующий WebCon интерфейс доступа к менеджеру.
 * @author Валентин А. Алексеев
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: WCConsoleObject.java,v 1.9 2004/06/21 17:09:25 valeks Exp $
 */
public class WCConsoleObject extends StandartODObject {
  /** Собственно сервлет-обработчик. */
  WCConsoleServlet servlet = null;
  /** Обработчик входящих сообщений.
   * @param msg сообщение
   */
  public final void handleMessage(final Message msg) {
    if (ODObjectLoadedMessage.equals(msg)) {
      servlet = new WCConsoleServlet();
      Message m = dispatcher.getNewMessage();
      WCAddServletMessage.setup(m, "webcon", getObjectName(), 0);
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
  /** Выход.
   * @param type признак выхода
   * @return код возврата
   */
  public final int cleanUp(final int type) {
    logger.finest("Cleaning up...");
    return 0;
  }
  /** Конструктор объекта с заданным порядковым номером.
   * @param id номер
   */
  public WCConsoleObject(final Integer id) {
    super("wcconsole" + id);
  }
  /** Вернуть список сервисов.
   * @return список сервисов
   */
  public final String[] getProviding() {
    String[] res = {"wcconsole"};
    return res;
  }
  /** Вернуть список зависимостей.
   * @return список зависимостей
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
    /** Информация о сервлете. */
    public String getServletInfo() {
      return "WCConsoleServlet: servlet that act as ODISP console. <a href=\"/wcconsole\">Start servlet</a>";
    }

    /** Выполнение запроса. */
    public void service(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
      res.setStatus(HttpServletResponse.SC_OK);
      res.setContentType("text/xhtml");
      ServletOutputStream p = res.getOutputStream();
      p.println("<html>");
      p.println("\t<head>");
      p.println("\t\t<title>ODISP WebCon console</title>");
      if (req.getQueryString() == null) {
	// необходимо вывести фреймовую страницу
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
	    // пока просто выводить результат toString(true)
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
	  Message m = dispatcher.getNewMessage(message, objectName, getObjectName(), 0);
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

    /** Добавление сообщения в журнал. */
    public synchronized void messageReceived(final Message msg) {
      if (messages.size() == 30) {
	// удаление устаревших сообщений
	messages.remove(0);
      }
      messages.add(msg);
    }
  } // WCConsoleServlet
} // WCConsoleObject
