package org.valabs.odisp.standart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.valabs.odisp.common.Dispatcher;
import org.valabs.odisp.common.Message;
import org.valabs.odisp.common.ODObject;
import org.valabs.stdmsg.ODCleanupMessage;
import org.valabs.stdmsg.ODObjectLoadedMessage;

/** Менеджер объектов ODISP.
 * @author (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
 * @version $Id: ObjectManager.java,v 1.40 2004/08/30 10:07:12 valeks Exp $
 */

class ObjectManager implements org.valabs.odisp.common.ObjectManager {
	/** Диспетчер объектов. */
	private Dispatcher dispatcher;
	/** Хранилище отложенных сообщений. */
	private DefferedMessages messages = new DefferedMessages();
	/** Список объектов. */
	private Map objects = new HashMap();
	/** Журнал. */
	private Logger log = Logger
			.getLogger("org.valabs.odisp.StandartObjectManager");
	/** Список сервисов менеджера. */
	private Map provided = new HashMap();
	/** Пул нитей отсылки. */
	private List senderPool = new ArrayList();
	/** Максимальное количество нитей создаваемых для отсылки изначально. */
	public static final int SENDER_POOL_SIZE = 5;
	/** Общее число объектов. */
	private int objCount = 0;
	/** Хранилище для сообщений. */
	private List messageStorage = new ArrayList();
	/** Статистика загрузки объектов. */
	private List statLoadedOrder = new ArrayList();
	/** Ранее загруженный файл hints. */
	private List hints = null;
	/** Кол-во объектов для загрузки. */
	private int statToLoadCount = 0;
	/** Статистика количества запусков loadPending. */
	private int statLoadPendingFireCount = 0;
	/** Добавление объекта как провайдера конкретного сервиса.
	 * @param service название сервиса
	 * @param objectName название объекта
	 */
	public void addProvider(final String service, final String objectName) {
		if (!provided.containsKey(service)) {
			provided.put(service, new ArrayList());
		}
		((List) provided.get(service)).add(objectName);
	}

	/** Удаление провайдера конкретного сервиса.
	 * В случае если у сервиса не остается ни одного провайдера -- он автоматически будет удален.
	 * @param service название сервиса
	 * @param objectName название объекта
	 */
	public void removeProvider(final String service, final String objectName) {
		if (provided.containsKey(service)) {
			((List) provided.get(service)).remove(objectName);
			if (((List) provided.get(service)).size() == 0) {
				provided.remove(service);
			}
		}
	}

	/** Проверка на существование провайдеров сервиса.
	 * @param service название сервиса
	 * @return флаг присутствия сервиса
	 */
	private boolean hasProviders(final String service) {
		return provided.containsKey(service);
	}

	/** Получить список объектов-провайдеров сервиса.
	 * @param service имя сервиса
	 * @return немодифицируемый thread-safe список объектов
	 */
	private List getProviders(final String service) {
		if (provided.containsKey(service)) {
			return Collections.unmodifiableList(Collections
					.synchronizedList((List) provided.get(service)));
		} else {
			return null;
		}
	}

	/** Получить список сервисов диспетчера.
	 * @return немодифицируемый список сервисов
	 */
	public List getProviding() {
		return new ArrayList(Collections.unmodifiableSet(provided.keySet()));
	}

	/** Попытка подгрузки объектов в следствии изменения списка сервисов менеджера. */
	public final void loadPending() {
		statLoadPendingFireCount++;
		// resources
		Map resourceList = new HashMap(dispatcher.getResourceManager()
				.getResources());
		Iterator it = resourceList.keySet().iterator();
		while (it.hasNext()) {
			String objectName = (String) it.next();
			if (!hasProviders(objectName)) {
				// ресурсы считаются провайдерами сервиса с собственным именем
				addProvider(objectName, objectName);
				log.fine("added resource provider " + objectName);
				statLoadedOrder.add(objectName);
			}
		}
		int loaded = 0;
		Map localObjects = null;
		synchronized (objects) {
			if (hints == null) {
				try {
					List hints = new ArrayList();
					File hintsFile = new File("hints");
					BufferedReader in = new BufferedReader(new FileReader(hintsFile));
					String s = in.readLine();
					while (s != null) {
						hints.add(s);
						s = in.readLine();
					}
				} catch (IOException e) {
					hints = null;
				}
			}
			if (hints != null) {
				localObjects = new TreeMap(new HintsOrderComparator(hints));
				localObjects.putAll(objects);
			} else {
				localObjects = new HashMap(objects);
			}
		}
		it = localObjects.keySet().iterator();
		while (it.hasNext()) {
			String objectName = (String) it.next();
			ObjectEntry oe = (ObjectEntry) objects.get(objectName);
			if (oe.isLoaded()) {
				continue;
			}
			log.config("trying to load object " + objectName);
			int numRequested = oe.getDepends().length;
			for (int i = 0; i < oe.getDepends().length; i++) {
				if (hasProviders(oe.getDepends()[i])) {
					numRequested--;
				} else {
					log.finer("dependency not met: " + oe.getDepends()[i]);
				}
			}
			if (numRequested == 0) {
				for (int i = 0; i < oe.getProvides().length; i++) {
					log.fine("added as provider of " + oe.getProvides()[i]);
					addProvider(oe.getProvides()[i], oe.getObject()
							.getObjectName());
				}
				oe.setLoaded(true);
				flushDefferedMessages(oe.getObject().getObjectName());
				log.config(" ok. loaded = " + objectName);
				statLoadedOrder.add(oe.getObject().getClass().getName());
				Message m = dispatcher.getNewMessage();
				ODObjectLoadedMessage.setup(m, objectName, 0);
				m.setDestination(objectName);
				oe.getObject().handleMessage(m);
				loaded++;
				statToLoadCount--;
			}
		}
		if (loaded > 0) {
			loadPending();
			if (statToLoadCount == 0) {
				// вывести удачный порядок загрузки.
				String msg = "\n============================================\n";
				msg += "loadPending() fire count: " + statLoadPendingFireCount;
				msg += "Loaded order:\n";
				it = statLoadedOrder.iterator();
				while (it.hasNext()) {
					String elt = (String) it.next();
					msg += "\t" + elt + "\n";
				}
				msg += "Total: " + statLoadedOrder.size() + "\n";
				msg += "Preparing hints...\n";
				List resultHints = new ArrayList();
				synchronized (objects) {
					resultHints.addAll(dispatcher.getResourceManager().getResources().keySet());
					it = statLoadedOrder.iterator();
					while (it.hasNext()) {
						String className = (String) it.next();
						Iterator iit = objects.keySet().iterator();
						while (iit.hasNext()) {
							ObjectEntry element = (ObjectEntry) objects.get(iit.next());
							if (element.getObject().getClass().getName().equals(className)) {
								if (!element.isIntoHints()) {
									msg += "\t" + element.getObject().getObjectName() + " was not ordered to be placed into hints\n";
									break;
								} else if (!resultHints.contains(className)) {
									resultHints.add(className);
								}
							}
						}
					}
				}
				if (resultHints.size() > 0) { 
					msg += "Result hints file:\n";
					/** @todo. HACK файл hints пишется в текущий каталог, что не есть гут. */
					try {
						File hintsFile = new File("hints");
						hintsFile.createNewFile();
						PrintStream out = new PrintStream(new FileOutputStream(hintsFile));
						it = resultHints.iterator();
						while (it.hasNext()) {
							String elt = (String) it.next();
							out.println(elt);
							msg += "\t" + elt + "\n";
						}
					} catch (IOException e) {
						log.warning("Unable to write hints file.");
						dispatcher.getExceptionHandler().signalException(e);
					}
					msg += "Total: " + resultHints.size() + "\n";
					msg += "============================================\n";
					log.fine(msg);
				}
			}
		} 
	}

	/** Динамическая загрузка объекта (с учётом зависимостей).
	 * Сохранение порядка в hints включено.
	 * @param cName имя загружаемого класса
	 * @param configuration список параметров загрузки
	 */
	public final void loadObject(final String cName, final Map configuration) {
		if (cName.equals(DispatcherHandler.class.getName())) {
			/** @todo. плохой хак. */
			loadObject(cName, configuration, false);
			return;
		}
		loadObject(cName, configuration, true);
	}
	
	/** Динамическая загрузка объекта (с учётом зависимостей).
	 * @param cName имя загружаемого класса
	 * @param configuration список параметров загрузки
	 * @param intoHints сохранять ли запись в файл hints
	 */
	public final void loadObject(final String cName, final Map configuration, final boolean intoHints) {
		log.config("loading object " + cName);
		try {
			Object[] params = new Object[1];
			objCount += (int) (Math.random() * 10000);
			params[0] = new Integer(objCount);
			Class[] dParams = new Class[1];
			dParams[0] = params[0].getClass();
			ODObject load = (ODObject) Class.forName(cName).getConstructor(
					dParams).newInstance(params);
			load.setDispatcher(dispatcher);
			load.setConfiguration(configuration);
			synchronized (objects) {
				ObjectEntry oe = new ObjectEntry(cName, load.getDepends(), load
						.getProviding());
				oe.setObject(load);
				oe.setLoaded(false);
				oe.setIntoHints(intoHints);
				objects.put(load.getObjectName(), oe);
			}
			statToLoadCount++;
		} catch (Exception e) {
			dispatcher.getExceptionHandler().signalException(e);
		}
	}

	/** Принудительная выгрузка объекта и вызов сборщика.
	 * мусора, так же учитываются зависимости:
	 * <ul>
	 * <li> Составление списка зависимых объектов
	 * <li> Удаление зависимых объектов
	 * <li> Удаление самого объекта
	 * </ul>
	 * @param objectName внутреннее имя объекта для удаления.
	 * @param code код выхода (при code != 0 зависимые объекты
	 * не удаляются).
	 */
	public synchronized final void unloadObject(final String objectName,
			final int code) {
		if (objects.containsKey(objectName)) {
			ObjectEntry oe = (ObjectEntry) objects.get(objectName);
			String[] provides = oe.getProvides();
			Iterator it = objects.keySet().iterator();
			List dependingObjs = new ArrayList();

			while (it.hasNext()) {
				String className = (String) it.next();
				String[] depends = ((ObjectEntry) objects.get(className))
						.getDepends();
				for (int i = 0; i < provides.length; i++) {
					for (int j = 0; j < depends.length; j++) {
						if (provides[i].equals(depends[j])
								&& !dependingObjs.contains(className)) {
							dependingObjs.add(className);
						}
					}
					// не забываем удалить объект из списков провайдеров
					removeProvider(provides[i], objectName);
				}
			}
			if (code == 0) {
				it = dependingObjs.iterator();
				while (it.hasNext()) {
					String className = (String) it.next();
					if (objects.containsKey(className)) {
						log.fine("removing " + objectName + "'s dependency "
								+ className);
						unloadObject(className, code);
					}
				}
			}
			ODObject obj = oe.getObject();
			Message m = dispatcher.getNewMessage();
			ODCleanupMessage.setup(m, objectName, 0);
			ODCleanupMessage.setReason(m, new Integer(code));
			dispatcher.send(m);
			objects.remove(objectName);
			log.config("\tobject " + objectName + " unloaded");
		}
	}

	/** Доступ к списку объектов. 
	 * @return список объектов
	 */
	public final Map getObjects() {
		return objects;
	}

	/** Констурктор менеджера.
	 * @param newDispatcher диспетчер для которого производится управление ресурсами
	 */
	public ObjectManager(final Dispatcher newDispatcher) {
		dispatcher = newDispatcher;
		for (int i = 0; i < SENDER_POOL_SIZE; i++) {
			senderPool.add(new Sender(this));
		}
	}

	/** Послать сообщение конкретному объекту.
	 * @param objectName имя объекта
	 * @param message сообщение
	 */
	private void sendToObject(final String objectName, final Message message) {
		ObjectEntry oe = null;
		// исключить модификацию списка дескрипторов объектов
		synchronized (objects) {
			oe = (ObjectEntry) objects.get(objectName);
		}
		if (oe == null) {
			return;
		}
		ODObject objToSendTo = null;
		// исключить модификацию дескриптора состояние объекта
		synchronized (oe) {
			if (!oe.isLoaded()) {
				log.finest("deffered message " + message.getAction() + " for "
						+ objectName);
				messages.addMessage(objectName, message);
				return;
			}
			objToSendTo = oe.getObject();
		}
		synchronized (messageStorage) {
			if (message.isOOB()) {
				log.finest("Sending OOB message " + message);
				messageStorage.add(0, new SendRecord(message, objToSendTo));
			} else {
				messageStorage.add(new SendRecord(message, objToSendTo));
			}
		}
	}

	/** Посылка сообщения всем объектам менеджера.
	 * @param message сообщение
	 */
	public final void send(Message message) {
		if (message == null || message.getAction().length() == 0
				|| !message.isCorrect()) {
			return;
		}
		// Получатели, ибо использовать глобольный итератор без глобальной
		// блокировки объекта по меньшей мере - наивно ;-))) 
		List recipients = null;
		// Посылка производится сервису.
		boolean serviceMatch = true;
		// в случае если получатель смахивает на имя сервиса
		// -- разослать только провайдерам, а не всем подряд
		if (hasProviders(message.getDestination())) {
			List providers = new ArrayList(getProviders(message
					.getDestination()));
			if (providers != null) {
				recipients = providers;
			}
		}
		if (recipients == null) {
			serviceMatch = false;
			recipients = new ArrayList();
			synchronized (objects) {
				Iterator it = objects.keySet().iterator();
				while (it.hasNext()) {
					String key = (String) it.next();
					ObjectEntry oe = (ObjectEntry) objects.get(key);
					if (Pattern.matches(oe.getObject().getMatch(), message
							.getDestination())
							|| Pattern.matches(message.getDestination(), oe
									.getObject().getObjectName())) {
						recipients.add(key);
					}
				}
			}
		}
		Iterator it = recipients.iterator();
		while (it.hasNext()) {
			String objectName = (String) it.next();
			if (serviceMatch) {
				message.setDestination(objectName);
			}
			sendToObject(objectName, message);
		}
	}

	/** Сброс записанных сообщений при снятии блокировки с объекта.
	 * @param objectName имя объекта
	 */
	private void flushDefferedMessages(final String objectName) {
		if (!objects.containsKey(objectName)) {
			return;
		}
		List toFlush = messages.flush(objectName);
		Iterator it = toFlush.iterator();
		while (it.hasNext()) {
			sendToObject(objectName, (Message) it.next());
		}
		loadPending();
	}

	/** Получение следующего сообщения для обработки. */
	public final SendRecord getNextPendingMessage() {
		SendRecord toSend = null;
		synchronized (messageStorage) {
			if (messageStorage.size() > 0) {
				toSend = (SendRecord) messageStorage.get(0);
				messageStorage.remove(0);
			}
		}
		return toSend;
	}
	public final void signalException(Exception e) {
		dispatcher.getExceptionHandler().signalException(e);
	}
	
	class HintsOrderComparator implements Comparator {
		private List hints;
		public HintsOrderComparator(final List hintsList) {
			hints = hintsList;
		}
		public int compare(Object _o1, Object _o2) {
			String o1 = ((ObjectEntry) objects.get(_o1)).getClassName();
			String o2 = ((ObjectEntry) objects.get(_o2)).getClassName();
			if (o1.equals(o2)) {
				return 0;
			}
			if (o1.equals(DispatcherHandler.class.getName())) {
				return 1;
			}
			if (o2.equals(DispatcherHandler.class.getName())) {
				return -1;
			}
			if (hints != null) {
				if (hints.contains(o1) && hints.contains(o2)) {
					return (hints.indexOf(o1) < hints.indexOf(o2)) ? -1 : 1;
				} else if (hints.contains(o1)) {
					return 1;
				} else if (hints.contains(o2)) {
					return -1;
				}
			}
			return 0;
		}
}
} // StandartObjectManager
