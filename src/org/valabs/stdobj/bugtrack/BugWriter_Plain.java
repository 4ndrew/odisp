package org.valabs.stdobj.bugtrack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;


/** Запись сообщения об ошибке в файл в виде простого отформатированного текста.
 * @author (C) 2005 <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>
 * @version $Id: BugWriter_Plain.java,v 1.1 2005/01/11 20:37:59 valeks Exp $
 */
public class BugWriter_Plain extends BugWriter {

  public BugWriter_Plain(Map cfg) {
    super(cfg);
  }
  
  public void writeBugReport(String id, String pc, String ai, SystemSnapshot ss) {
    File f = new File("bug-" + DateFormat.getInstance().format(Calendar.getInstance().getTime()));
    try {
      if (!f.exists()) {
        f.createNewFile();
      }
      PrintWriter out = new PrintWriter(new FileWriter(f));
      out.println("ODISP Bug Report");
      out.println("Bug occured at: " + DateFormat.getInstance().format(Calendar.getInstance().getTime()));
      if (id != null) {
        out.println("Bug identification: " + id);
      }
      if (pc != null) {
        out.println("Probable cause: " + pc);
      }
      if (ai != null) {
        out.println("Additional information: " + ai);
      }
      out.println("==================================================================");
      out.println("System snapshot at the time of bug.");
      out.println("Modules:");
      Iterator it = ss.getModuleAboutList().iterator();
      while (it.hasNext()) {
        out.println("  - " + it.next());
      }
      out.println("Modules status:");
      it = ss.getModuleStatusList().iterator();
      while (it.hasNext()) {
        out.println("  - " + it.next());
      }
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
