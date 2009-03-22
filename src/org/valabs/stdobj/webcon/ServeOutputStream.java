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
import java.io.OutputStream;
import java.io.PrintStream;

import org.valabs.stdobj.webcon.servlet.ServletOutputStream;

class ServeOutputStream extends ServletOutputStream {

  private PrintStream out;
  private ServeConnection conn;

  public ServeOutputStream(OutputStream out, ServeConnection conn) {
    this.out = new PrintStream(out);
    this.conn = conn;
  }

  public void write(int b) throws IOException {
    conn.writeHeaders();
    out.write(b);
  }

  public void write(byte[] b, int off, int len) throws IOException {
    conn.writeHeaders();
    out.write(b, off, len);
  }

  public void flush() throws IOException {
    conn.writeHeaders();
    out.flush();
  }

  public void close() throws IOException {
    conn.writeHeaders();
    out.close();
  }

  public void print(String s) throws IOException {
    conn.writeHeaders();
    out.print(s);
  }

  public void print(int i) throws IOException {
    conn.writeHeaders();
    out.print(i);
  }

  public void print(long l) throws IOException {
    conn.writeHeaders();
    out.print(l);
  }

  public void println(String s) throws IOException {
    conn.writeHeaders();
    out.println(s);
  }

  public void println(int i) throws IOException {
    conn.writeHeaders();
    out.println(i);
  }

  public void println(long l) throws IOException {
    conn.writeHeaders();
    out.println(l);
  }

  public void println() throws IOException {
    conn.writeHeaders();
    out.println();
  }
}
