package org.valabs.odisp;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;


/** Стартовый класс для диспетчера.
 * @author <a hrev="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>
 * @version $Id: Launcher.java,v 1.1 2005/01/27 14:15:44 valeks Exp $
 */
public class Launcher {

  /**
   * 
   */
  public Launcher(String[] args) {
    String dispatcher = "standart";
    List libdirs = new ArrayList();
    int i = 0;
    for (; i < args.length; i++) {
      if (args[i].startsWith("--dispatcher")) {
        dispatcher = args[i].split("=")[1];
      } else if (args[i].startsWith("--libdir")) {
        libdirs.add(args[i].split("=")[1]);
      } else if (args[i].equals("--")) {
        i++;
        break;
      }
    }
    List newArgs = Arrays.asList(args).subList(i, args.length);
    URL[] jars = searchJarURLs(libdirs);
    System.setProperty("odisp.libchecksum", folderChecksum(libdirs.toString()));
    System.setProperty("odisp.libdirs", libdirs.toString());
    System.out.println("Library folders checksummed: " + System.getProperty("odisp.libdirs"));
    System.out.println("Library folders checksum is: " + System.getProperty("odisp.libchecksum"));
    ClassLoader ucl = new URLClassLoader(jars);
    Class[] paramClass = {List.class};
    Object[] params = {newArgs};
    try {
      ucl.loadClass("org.valabs.odisp." + dispatcher + ".Dispatcher").getConstructor(paramClass).newInstance(params);
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }
  
  public static String folderChecksum(String s_libdirs) {
    String result = "";
    s_libdirs = s_libdirs.substring(1, s_libdirs.length() - 1);
    StringTokenizer st = new StringTokenizer(s_libdirs, ",");
    while (st.hasMoreTokens()) {
      String libdir = st.nextToken();
      libdir = libdir.trim();
      File ld = new File(libdir);
      if (ld.isDirectory()) {
        String files[] = ld.list(new FilenameFilter() {
          public boolean accept(File arg0, String arg1) {
            return arg1.endsWith("jar");
          }
        });
        for (int i = 0; i < files.length; i++) {
          File f = new File(ld.getAbsolutePath() + File.separator + files[i]);
          result += f.getName() + f.length();
        }
      }
    }    
    return result;
  }
  
  /**
   * @param libdirs
   * @return
   * @throws MalformedURLException
   */
  private URL[] searchJarURLs(List libdirs) {
    Iterator it = libdirs.iterator();
    List resultUrls = new ArrayList();
    while (it.hasNext()) {
      String libdir = (String) it.next();
      File ld = new File(libdir);
      if (ld.isDirectory()) {
        String files[] = ld.list(new FilenameFilter() {
          public boolean accept(File arg0, String arg1) {
            return arg1.endsWith("jar");
          }
        });
        for (int i = 0; i < files.length; i++) {
          resultUrls.add("file://" + ld.getAbsolutePath() + File.separator + files[i]);
        }
      }
    }
    URL[] urls = new URL[resultUrls.size()];
    for (int i = 0; i < urls.length; i++) {
      try {
        urls[i] = new URL((String) resultUrls.get(i));
        System.out.println("Found jar: " + urls[i]);
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    }
    return urls;
  }

  public static void main(String[] args) {
    new Launcher(args);
  }
}
