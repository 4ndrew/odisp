package org.valabs.odisp.standart;

import java.io.FileInputStream;
import java.util.Hashtable;


/**
 * @author valeks
 * @version $Id: ReloadableClassLoader.java,v 1.1 2005/01/11 20:47:14 valeks Exp $
 */
public class ReloadableClassLoader extends ClassLoader {
  private String baseDir;
  private static ReloadableClassLoader impl;
  private Hashtable classes = new Hashtable();
  
  public static ReloadableClassLoader getInstance(String _baseDir) {
    if (impl == null) {
      impl = new ReloadableClassLoader(_baseDir);
    }
    return impl;
  }
  
  private ReloadableClassLoader(String _baseDir) {
    baseDir = _baseDir;
  }
  /**
   * This sample function for reading class implementations reads
   * them from the local file system
   */
  private byte getClassImplFromDataBase(String className)[] {
      System.out.println("        >>>>>> Fetching the implementation of "+className);
      byte result[];
      try {
          FileInputStream fi = new FileInputStream(baseDir + className.replace('.', '/') + ".class");
          result = new byte[fi.available()];
          fi.read(result);
          return result;
      } catch (Exception e) {
        	e.printStackTrace();
          return null;
      }
  }

  /**
   * This is a simple version for external clients since they
   * will always want the class resolved before it is returned
   * to them.
   */
  public Class loadClass(String className) throws ClassNotFoundException {
      return (loadClass(className, true));
  }

  /**
   * This is the required version of loadClass which is called
   * both from loadClass above and from the internal function
   * FindClassFromClass.
   */
  public synchronized Class loadClass(String className, boolean resolveIt)
      throws ClassNotFoundException {
      Class result;
      byte  classData[];


      /* Check with the primordial class loader */
      if (!className.startsWith("com")) {
        try {
          result = super.findSystemClass(className);
          return result;
        } catch (ClassNotFoundException e) {
        }
      }
      if (classes.containsKey(className)) {
        System.out.println("        >>>>>> Serving from cache : "+className);
        return (Class) classes.get(className);
      }
      System.out.println("        >>>>>> Load class : "+className);

      /* Try to load it from our repository */
      classData = getClassImplFromDataBase(className);
      if (classData == null) {
          throw new ClassNotFoundException();
      }

      /* Define it (parse the class file) */
      result = defineClass(className, classData, 0, classData.length);
      if (result == null) {
          throw new ClassFormatError();
      }
      classes.put(className, result);
      if (resolveIt) {
          resolveClass(result);
      }
      return result;
  }
}
