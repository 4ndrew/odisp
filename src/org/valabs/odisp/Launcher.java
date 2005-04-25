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
 * @version $Id: Launcher.java,v 1.3 2005/04/25 13:55:09 valeks Exp $
 */
public class Launcher {

  /**
   * 
   */
  public Launcher(String[] args) {
    String dispatcher = "standart";
    List libdirs = new ArrayList();
    int launcherArgs = 0;
    for (; launcherArgs < args.length; launcherArgs++) {
      if (args[launcherArgs].startsWith("--dispatcher")) {
        dispatcher = args[launcherArgs].split("=")[1];
      } else if (args[launcherArgs].startsWith("--libdir")) {
        libdirs.add(args[launcherArgs].split("=")[1]);
      } else if (args[launcherArgs].equals("--")) {
        launcherArgs++;
        break;
      }
    }
    List newArgs = Arrays.asList(args).subList(launcherArgs, args.length);
    URL[] jars = searchJarURLs(libdirs);
    System.setProperty("odisp.libchecksum", folderChecksum(libdirs.toString()));
    System.setProperty("odisp.libdirs", libdirs.toString());
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
  
  public static final String folderChecksum(final String s_libdirs) {
    String result = "";
  	if (s_libdirs != null) {
			final StringTokenizer libraryDirs = new StringTokenizer(s_libdirs
					.substring(1, s_libdirs.length() - 1), ",");
			final FilenameFilter jarFiles = new FilenameFilter() {
				public boolean accept(File arg0, String arg1) {
					return arg1.endsWith("jar");
				}
			};
			while (libraryDirs.hasMoreTokens()) {
				final File libraryDir = new File(libraryDirs.nextToken().trim());
				if (libraryDir.isDirectory()) {
					final String files[] = libraryDir.list(jarFiles);
					for (int i = 0; i < files.length; i++) {
						final File file = new File(libraryDir.getAbsolutePath()
								+ File.separator + files[i]);
						result += file.getName() + file.length();
					}
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
  private final URL[] searchJarURLs(final List libdirs) {
    final Iterator libdirIt = libdirs.iterator();
    final List resultUrls = new ArrayList();
    final FilenameFilter jarFiles = new FilenameFilter() {
      public boolean accept(File arg0, String arg1) {
        return arg1.endsWith("jar");
      }
    };
    while (libdirIt.hasNext()) {
      final String libdir = (String) libdirIt.next();
      final File libraryDir = new File(libdir);
      if (libraryDir.isDirectory()) {
        final String files[] = libraryDir.list(jarFiles);
        for (int i = 0; i < files.length; i++) {
          resultUrls.add("file://" + libraryDir.getAbsolutePath() + File.separator + files[i]);
        }
      }
    }
    URL[] urls = new URL[resultUrls.size()];
    for (int i = 0; i < urls.length; i++) {
      try {
        urls[i] = new URL((String) resultUrls.get(i));
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    }
    return urls;
  }

  public static final void main(final String[] args) {
    new Launcher(args);
  }
}
