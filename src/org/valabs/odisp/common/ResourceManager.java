package org.valabs.odisp.common;

import java.util.List;
import java.util.Map;

/**
 * Интерфейс менеджера ресурсов ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev
 *         </a>
 * @version $Id: ResourceManager.java,v 1.11 2005/01/25 20:34:33 valeks Exp $
 */

public interface ResourceManager {
  /**
   * Загрузка ресурса.
   * @param className имя класса
   * @param mult множитель
   */
  void loadResource(String className, int mult, Map params);

  /**
   * Получить список ресурсов.
   * @return карта ресурсов
   */
  Map getResources();

  /**
   * Вернуть статистику по ресурсам.
   * @return Список строк.
   */
  List statRequest();

  /**
   * Блокирующая попытка захвата ресурса напрямую. <b>ВНИМАНИЕ! </b> так как,
   * обычно, захваты производятся в контексте обработчиков сообщений (а,
   * соответственно, в нитях Sender), блокирующий вызов должен использоваться
   * чрезвычайно осторожно иначе возможна ситуация полной блокировки системы в
   * случае одновременных невыполнимых запросов. Для того, что бы попытаться
   * избежать этих проблем, а так же не занимать попусту нитки Sender,
   * необходимо либо пользоваться неблокирующим вызовом, либо реализовывать
   * захват ресурса в отдельной нити, например так: 
   * <pre>
   * ...
   * SomeResource res = null;
   * new Thread() {
   *   public void run() {
   * 		setBlockedState(true);
   * 		res = dispatcher.resourceAcquire(SomeResource.class.getName());
   * 		setBlockedState(false);
   * 	}
   * }.start();
   * ...
   * </pre>
   * Таким образом собственно захват ресурса производится в отдельном потоке при
   * заблокированном состоянии объекта.
   * @param className имя ресурса
   * @return ссылка на ресурс
   */
  Resource resourceAcquire(String className);

  /**
   * Неблокирующая попытка захвата ресурса напрямую.
   * @param className имя ресурса
   * @return ссылка не ресурс или null если ресурс заблокирован
   */
  Resource resourceTryAcquire(String className);

  /**
   * Высвобождение ресурса.
   * @param className имя ресурса
   * @param resource ссылка на ресурс
   */
  void releaseResource(String className, Resource resource);
} // ResourceManager
