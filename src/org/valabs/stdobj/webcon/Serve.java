/*
 * Serve - minimal Java HTTP server class
 *
 * Copyright (C)1996,1998 by Jef Poskanzer <jef@acme.com>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * Visit the ACME Labs Java page for up-to-date versions of this and other
 * fine Java utilities: http://www.acme.com/java/
 */
package org.valabs.stdobj.webcon;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.valabs.stdobj.webcon.servlet.Servlet;
import org.valabs.stdobj.webcon.servlet.ServletContext;
import org.valabs.stdobj.webcon.servlet.ServletException;

/** Minimal Java HTTP server class by ACME.
 * <p> Being largerly abandoned ACME Java Serve was taken as the core of WebCon for ODISP project
 * by Valentin A. Alekseev. Original code was split into several classes, removed dependencies on
 * now useless ACME Labs packages which were replaced by the modern ones. Also SSL support via JSSE
 * was introduced.
 * </p>
 * @see org.valabs.stdobj.webcon.servlet.http.HttpServlet
 * @author (C) 1996,1998 by Jef Poskanzer <jef@acme.com>
 * @author (C) 2004 Valentin A. Alekseev
 * @version $Id: Serve.java,v 1.11 2005/07/22 13:06:54 dron Exp $
 */

public class Serve implements ServletContext {
  private int port;
  private PrintStream logStream;
  WildcardDictionary registry;

  /** Constructor. 
   * @param port port to listen to
   * @param logStream where to output log messages
   */
  public Serve(int port, PrintStream logStream) {
    this.port = port;
    this.logStream = logStream;
    registry = new WildcardDictionary();
  }

  /** Constructor, default log stream. 
   * Same as above, but uses System.err as log stream
   * @param port port ot listen to
   */
  public Serve(int port) {
    this(port, System.err);
  }

  /** Constructor, default port and log stream.
   * Construct with all the defaults -- port 9090 and System.err as log output.
   */
  public Serve() {
    this(9090, System.err);
  }

  /** Register a Servlet by class name.  Registration consists of a URL
   * pattern, which can contain wildcards, and the class name of the Servlet
   * to launch when a matching URL comes in.  Patterns are checked for
   * matches in the order they were added, and only the first match is run.
   * @param urlPat pattern for URLs that will match given servlet
   * @param className class name of servlet that will handle requests
   */
  public void addServlet(String urlPat, String className) {
    // See if we have already instantiated this one.
    Servlet servlet = (Servlet) servlets.get(className);
    if (servlet != null) {
      addServlet(urlPat, servlet);
      return;
    }

    // Check if we're allowed to make one of these.
    SecurityManager security = System.getSecurityManager();
    if (security != null) {
      int i = className.lastIndexOf('.');
      if (i != -1) {
        security.checkPackageAccess(className.substring(0, i));
        security.checkPackageDefinition(className.substring(0, i));
      }
    }

    // Make a new one.
    try {
      servlet = (Servlet) Class.forName(className).newInstance();
      addServlet(urlPat, servlet);
      return;
    } catch (ClassNotFoundException e) {
      log("Class not found: " + className);
    } catch (ClassCastException e) {
      log("Class cast problem: " + e.getMessage());
    } catch (InstantiationException e) {
      log("Instantiation problem: " + e.getMessage());
    } catch (IllegalAccessException e) {
      log("Illegal class access: " + e.getMessage());
    } catch (Exception e) {
      log("Unexpected problem creating servlet: " + e);
    }
  }

  /** Register a Servlet.  Registration consists of a URL pattern,
   * which can contain wildcards, and the Servlet to
   * launch when a matching URL comes in.  Patterns are checked for
   *  matches in the order they were added, and only the first match is run.
   */
  public void addServlet(String urlPat, Servlet servlet) {
    try {
      servlet.init(new ServeConfig(this));
      registry.put(urlPat, servlet);
      servlets.put(servlet.getClass().getName(), servlet);
    } catch (ServletException e) {
      log("Problem initializing servlet: " + e);
    }
  }

  /** Remove servlet by reference. */
  public final void removeServlet(final Servlet servlet) {
    registry.removeValue(servlet);
    ((Servlet) servlets.get(servlet.getClass().getName())).destroy();
    servlets.remove(servlet.getClass().getName());
  }

  /** Хранилище для клиентских ключей. */
  private KeyStore clientKeyStore;
  /** Хранилище для ключей сервера. */
  private KeyStore serverKeyStore;

  /** SSL контекст. */
  private SSLContext sslContext;

  /** Источник случайных чисел. */
  private static SecureRandom secureRandom;

  /** Пароль к хранилищу ключей. */
  private static final String PASSPHRASE = "spasswd";

  /** Инициализация хранилища SSL ключей клиента.
   * @throws GeneralSecurityException в случае ошибок при работе с хранилищем ключей
   * @throws IOException в случае недоступности хранилища
   */
  private void setupClientKeyStore()
    throws GeneralSecurityException, IOException {
    clientKeyStore = KeyStore.getInstance("JKS");
    clientKeyStore.load(
      new FileInputStream("client.public"),
      "public".toCharArray());
  }

  /** Инициализация хранилища SSL ключей сервера.
   * @throws GeneralSecurityException в случае ошибок при работе с хранилищем ключей
   * @throws IOException в случае недоступности хранилища
   */
  private void setupServerKeystore()
    throws GeneralSecurityException, IOException {
    serverKeyStore = KeyStore.getInstance("JKS");
    serverKeyStore.load(
      new FileInputStream("server.private"),
      PASSPHRASE.toCharArray());
  }

  /** Инициализация SSL контекста.
   * @throws GeneralSecurityException в случае проблем с инциализацией контекста
   */
  private void setupSSLContext() throws GeneralSecurityException {
    TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
    tmf.init(clientKeyStore);

    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
    kmf.init(serverKeyStore, PASSPHRASE.toCharArray());

    sslContext = SSLContext.getInstance("TLS");
    sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), secureRandom);
  }

  /** Run the server.  Returns only on errors. */
  public void serve() {
    ServerSocket serverSocket = null;
    try {
      SSLServerSocket ss;
      setupClientKeyStore();
      setupServerKeystore();
      setupSSLContext();
      SSLServerSocketFactory sf = sslContext.getServerSocketFactory();
      ss =
        (SSLServerSocket) sf.createServerSocket(
          port,
          5,
          InetAddress.getLocalHost());
      ss.setNeedClientAuth(false);
      serverSocket = ss;
    } catch (Exception gse) { /* игнорируется */  }
    if (serverSocket == null) {
      // в случае каких либо проблем с SSL сокетом пытаемся создать обычный.
      try {
        serverSocket = new ServerSocket(port, 5, InetAddress.getLocalHost());
      } catch (Exception e) {
        log(
          "Unable to create even non-SSL socket. Bailing out:" + e.toString());
        e.printStackTrace();
        return;
      }
    }
    try {
      while (true) {
        Socket socket = serverSocket.accept();
        new ServeConnection(socket, this);
      }
    } catch (IOException e) {
      log("Server socket: " + e);
      return;
    } finally {
      try {
        if (serverSocket != null) {
          serverSocket.close();
        }
        destroyAllServlets();
      } catch (IOException e) { /* игнорируется */ }
    }
  }

  // Methods from ServletContext.

  protected Hashtable servlets = new Hashtable();

  /** Gets a servlet by name.
   * @param name the servlet name
   * @return null if the servlet does not exist
   */
  public Servlet getServlet(String name) {
    return (Servlet) servlets.get(name);
  }

  /** Enumerates the servlets in this context (server). Only servlets that
   * are accesible will be returned.  This enumeration always includes the
   * servlet itself.
   */
  public Enumeration getServlets() {
    return servlets.elements();
  }

  /** Enumerates the names of the servlets in this context (server). Only
   * servlets that are accesible will be returned.  This enumeration always
   * includes the servlet itself.
   */
  public Enumeration getServletNames() {
    return servlets.keys();
  }

  /** Destroys all currently-loaded servlets. */
  public void destroyAllServlets() {
    Iterator it = servlets.keySet().iterator();
    while (it.hasNext()) {
      ((Servlet) servlets.get(it.next())).destroy();
    }
    servlets.clear();
  }

  /** Write information to the servlet log.
   * @param message the message to log
   */
  public void log(String message) {
    Date date = new Date(System.currentTimeMillis());
    logStream.println("[" + date.toString() + "] " + message);
  }

  /** Write a stack trace to the servlet log.
   * @param exception where to get the stack trace
   * @param message the message to log
   */
  public void log(Exception exception, String message) {
    // !!!
    log(message);
  }

  /** Applies alias rules to the specified virtual path and returns the
   * corresponding real path.  It returns null if the translation
   * cannot be performed.
   * @param path the path to be translated
   */
  public String getRealPath(String path) {
    // No mapping.
    return path;
  }

  /** Returns the MIME type of the specified file.
   * @param file file name whose MIME type is required
   */
  public String getMimeType(String file) {
    int lastDot = file.lastIndexOf('.');
    int lastSep = file.lastIndexOf(File.separatorChar);
    if (lastDot == -1 || (lastSep != -1 && lastDot < lastSep)) {
      return "text/plain; charset=iso-8859-1";
    }
    String extension = file.substring(lastDot + 1);
    if (extension.equals("html") || extension.equals("htm")) {
      return "text/html; charset=iso-8859-1";
    } else if (extension.equals("gif")) {
      return "image/gif";
    } else if (extension.equals("jpg") || extension.equals("jpeg")) {
      return "image/jpeg";
    } else if (extension.equals("au")) {
      return "audio/basic";
    } else if (extension.equals("ra") || extension.equals("ram")) {
      return "audio/x-pn-realaudio";
    } else if (extension.equals("wav")) {
      return "audio/wav";
    } else if (extension.equals("mpg") || extension.equals("mpeg")) {
      return "video/mpeg";
    } else if (extension.equals("qt") || extension.equals("mov")) {
      return "video/quicktime";
    } else if (extension.equals("class")) {
      return "application/octet-stream";
    } else if (extension.equals("ps")) {
      return "application/postscript";
    } else if (extension.equals("wrl")) {
      return "x-world/x-vrml";
    } else if (extension.equals("pac")) {
      return "application/x-ns-proxy-autoconfig";
    } else {
      return "text/plain; charset=iso-8859-1";
    }
  }

  /** Returns the name and version of the web server under which the servlet
   * is running.
   * Same as the CGI variable SERVER_SOFTWARE.
   */
  public String getServerInfo() {
    return ServeUtils.serverName
      + " "
      + ServeUtils.serverVersion
      + " ("
      + ServeUtils.serverUrl
      + ")";
  }

  /** Returns the value of the named attribute of the network service, or
   * null if the attribute does not exist.  This method allows access to
   * additional information about the service, not already provided by
   * the other methods in this interface.
   */
  public Object getAttribute(String name) {
    // This server does not support attributes.
    return null;
  }

}
