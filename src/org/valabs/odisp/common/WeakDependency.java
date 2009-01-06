package org.valabs.odisp.common;

/** Слабая зависимость от заданного компонента.
 * @author <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>
 * @version $Id$
 */
public final class WeakDependency {
  private WeakDependency() {}
  public static final String create(String objectName) {
    return "~" + objectName;
  }
  
  public static final boolean isWeakDependency(String objectName) {
    return objectName.startsWith("~");
  }
  
  public static final String getWeakDependency(String objectName) {
    assert isWeakDependency(objectName) : objectName + " is not a weak dependency";
    return objectName.substring(1);
  }
}
