/* SampleServlet - trivial servlet
 *
 * Copyright (C) 1996 by Jef Poskanzer <jef@acme.com>.  All rights reserved.
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

import java.io.IOException;

import org.valabs.stdobj.webcon.servlet.ServletException;
import org.valabs.stdobj.webcon.servlet.ServletOutputStream;
import org.valabs.stdobj.webcon.servlet.http.HttpServlet;
import org.valabs.stdobj.webcon.servlet.http.HttpServletRequest;
import org.valabs.stdobj.webcon.servlet.http.HttpServletResponse;

/** Trivial servlet. */
public class SampleServlet extends HttpServlet {

  /** Returns a string containing information about the author, version, and copyright of the servlet.
   * @see org.valabs.stdobj.webcon.servlet.Servlet#getServletInfo()
   */
  public String getServletInfo() {
    return "trivial servlet";
  }

  /** Services a single request from the client.
   * @param req the servlet request
   * @param res the servlet response
   * @exception ServletException when an exception has occurred
   * @exception IOException when an IO exception occured
   */
  public void service(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
    log("called");
    res.setStatus(HttpServletResponse.SC_OK);
    res.setContentType("text/html");
    ServletOutputStream p = res.getOutputStream();
    p.println("<HTML><HEAD>");
    p.println("<TITLE>Sample Servlet Output</TITLE>");
    p.println("</HEAD><BODY>");
    p.println("<H2>Sample Servlet Output</H2>");
    p.println("<P>Output from a sample servlet.");
    p.println("</BODY></HTML>");
    p.flush();
    p.close();
  }

}
