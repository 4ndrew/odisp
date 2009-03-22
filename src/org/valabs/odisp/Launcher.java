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


/**
 * ��������� ����� ��� ����������.
 * 
 * @author <a hrev="mailto:valeks@valabs.spb.ru">�������� �������� �.</a>
 * @version $Id: Launcher.java,v 1.5 2005/09/21 11:07:40 dron Exp $
 */
public class Launcher {

  /**
   * �����������.
   * 
   * @param args ��������� ��� ������. 
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
   * ����� ���� jar-��������� � ��������� �����.
   * 
   * @param libdirs ������ �����.
   * @return ������ jar-��������� � ������� ������.
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
          File newFile = new File(libraryDir.getAbsolutePath() + File.separator + files[i]);
          resultUrls.add(newFile);
        }
      }
    }
    URL[] urls = new URL[resultUrls.size()];
    for (int i = 0; i < urls.length; i++) {
      try {
        urls[i] = ((File) resultUrls.get(i)).toURL();
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    }
    return urls;
  }

  public static final void main(final String[] args) {
    // BasicConfigurator.configure();
    new Launcher(args);
  }
}
