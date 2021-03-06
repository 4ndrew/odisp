/* WildcardDictionary - a dictionary with wildcard lookups
 *
 * Copyright (C) 1996 by Jef Poskanzer <jef@acme.com>.  All rights reserved.
 * Copyright (C) 2004 by Valentin Alekseev <valeks@novel-il.ru> All rights reserved.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/** A dictionary with wildcard lookups. */

public class WildcardDictionary extends HashMap {
  /** Gets the object associated with the specified key in the dictionary.
   * The key is assumed to be a String, which is matched against
   * the wildcard-pattern keys in the dictionary.
   * @param key the string to match
   * @return the element for the key, or null if there's no match
   */
  public synchronized Object get(Object key) {
    Iterator it = keySet().iterator();
    while (it.hasNext()) {
      String tkey = (String) it.next();
      if (Pattern.matches((String) key, tkey)) {
        key = tkey;
        return super.get(tkey);
      }
    }
    return null;
  }

  /** ������� ������ �� ��������.
   * ��������� ��� ������ � ������� �������� ��������� � ��������.
   * @param value ��������
   */
  public void removeValue(final Object value) {
    if (containsValue(value)) {
      List toRemove = new ArrayList();
      Iterator it = keySet().iterator();
      while (it.hasNext()) {
        Object tkey = it.next();
        if (get(tkey).equals(value)) {
          toRemove.add(tkey);
        }
      }
      it = toRemove.iterator();
      while (it.hasNext()) {
        remove(it.next());
      }
    }
  }
}