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
package org.valabs.stdobj.bugtrack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;


/** ������ ��������� �� ������ � ���� � ���� �������� ������������������ ������.
 * @author (C) 2005 <a href="mailto:valeks@valabs.spb.ru">�������� �������� �.</a>
 * @version $Id: BugWriter_Plain.java,v 1.3 2005/07/04 13:05:26 valeks Exp $
 */
public class BugWriter_Plain extends AbstractBugWriter {

  public BugWriter_Plain(Map cfg) {
    super(cfg);
  }
  
  public void writeBugReport(final String id, final String pc, final String ai, final SystemSnapshot ss) {
    final File reportFile = new File("bug-" + DateFormat.getInstance().format(Calendar.getInstance().getTime()).replaceAll(" ", "_").replaceAll(":", "_"));
    try {
      if (!reportFile.exists()) {
        reportFile.createNewFile();
      }
      final PrintWriter report = new PrintWriter(new FileWriter(reportFile));
      report.println("ODISP Bug Report");
      report.println("Bug occured at: " + DateFormat.getInstance().format(Calendar.getInstance().getTime()));
      if (id != null) {
        report.println("Bug identification: " + id);
      }
      if (pc != null) {
        report.println("Probable cause: " + pc);
      }
      if (ai != null) {
        report.println("Additional information: " + ai);
      }
      report.println("==================================================================");
      report.println("System snapshot at the time of bug.");
      report.println("Modules:");
      Iterator it = ss.getModuleAboutList().iterator();
      while (it.hasNext()) {
        report.println("  - " + it.next());
      }
      report.println("Modules status:");
      it = ss.getModuleStatusList().iterator();
      while (it.hasNext()) {
        report.println("  - " + it.next());
      }
      report.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
