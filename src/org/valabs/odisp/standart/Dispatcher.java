package com.novel.odisp;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.util.regex.*;

import com.novel.odisp.common.*;

/** Стандартный диспетчер ODISP.
 * Стандартный диспетчер реализует пересылку сообщений между объектами ядра
 * и управление ресурсными объектами.
 * @author Валентин А. Алексеев
 * @author (C) 2003, НПП "Новел-ИЛ"
 * @version $Id: Dispatcher.java,v 1.7 2003/10/12 20:01:12 valeks Exp $
 */
public class StandartDispatcher implements Dispatcher {
	
	Map objects = new HashMap();
	Map resources = new HashMap();
	DefferedMessages messages = new DefferedMessages();
	int obj_count = 0;
	/** Динамическая загрузка ресурсных объектов
	    @param className имя загружаемого класса
	    @param mult количество загружаемых объектов
	    @return void
	*/
	private void loadResource(String className, int mult){
	    System.err.print("\tloading resource "+className);
	    try {
		for(int i = 0; i < mult; i++){
		    resources.put(className+":"+i, Class.forName(className).newInstance());
		    System.out.print("+");
		}
		System.out.println(" ok.");
	    } catch(ClassNotFoundException e){
		System.err.println(" failed: "+e);
	    } catch(InstantiationException e){
	        System.err.println(" failed: "+e);
	    } catch(IllegalAccessException e){
	        System.err.println(" failed: "+e);
	    }	    
	}
	/** Выгрузка ресурсного объекта
	    
	*/
	private void unloadResource(String roName, int code){
	    if(resources.containsKey(roName)){
		Resource res = (Resource)resources.get(roName);
		res.cleanUp(code);
		resources.remove(roName);
	    }
	}
	/** Динамическая загрузка объекта
	 * @param className имя загружаемого класса
	 * @return void
	 */
	private void loadObject(String className){
	    System.err.print("\tloading object "+className);
	    try {
		Object params[] = new Object[1];
		params[0] = new Integer(obj_count++);
		Class declParams[] = new Class[1];
		declParams[0] = params[0].getClass();
		ODObject load = (ODObject)Class.forName(className).getConstructor(declParams).newInstance(params);
		Message m = getNewMessage("od_object_loaded",load.getObjectName(),"stddispatcher",0);
		load.setDispatcher(this);
		load.start();
		load.addMessage(m);
		synchronized (objects){
		    objects.put(load.getObjectName(),new ObjectEntry(className, false, load));
		}
		System.err.println(" ok. loaded="+load.getObjectName());
	    } catch(InvocationTargetException e){
		System.err.println(" failed: "+e);
	    } catch(NoSuchMethodException e){
		System.err.println(" failed: "+e);
	    } catch(ClassNotFoundException e){
		System.err.println(" failed: "+e);
	    } catch(InstantiationException e){
	        System.err.println(" failed: "+e);
	    } catch(IllegalAccessException e){
	        System.err.println(" failed: "+e);
	    } catch(IllegalArgumentException e){
	        System.err.println(" failed: "+e);
	    }
	}
	/** Принудительная выгрузка объекта и
	 * вызов сборщика мусора
	 * @param objectName внутреннее имя объекта для удаления
	 * @return void
	 */
	private void unloadObject(String objectName, int code){
	    if(objects.containsKey(objectName)){
		ObjectEntry oe = (ObjectEntry)objects.get(objectName);
		ODObject obj = oe.object;
		Message m = getNewMessage("od_cleanup", objectName, "stddispatcher", 0);
		m.addField(new Integer(code));
		sendMessage(m);
		obj.interrupt();
		objects.remove(oe);
		System.out.println("\tobject "+objectName+" unloaded");
	    }
	}
	/** Интерфейс для объектов ядра для отсылки сообщений.
	 * Реализует multicast рассылку сообщений
	 * @param message сообщение для отсылки
	 * @return void
	 */
	public void sendMessage(Message message){
	    if(message.getAction().length() == 0)
		return;
	    synchronized(objects){
		Iterator it = objects.keySet().iterator();
		while(it.hasNext()){
		    String cl_n = (String)it.next();
		    ObjectEntry oe = (ObjectEntry)objects.get(cl_n);
		    if(oe.blockedState){
			System.out.println("[d] message deffered for "+cl_n);
			messages.addMessage(cl_n, message);
			continue;
		    }
		    System.out.println("[d] message sent for "+cl_n);		
		    ODObject cl_send = oe.object;
		    cl_send.addMessage(message);
		    synchronized(cl_send){cl_send.notify();}
		}
	    }
	}
	/** Интерфейс создания нового сообщения для сокрытия конкретной реализации
	 * сообщений.
	 * @param action действие которое несет сообщение
	 * @param destination адресат сообщения ({@link java.util.regex.Pattern рег.выр.})
	 * @param origin отправитель сообщения
	 * @param inReplyTo идентификатор сообщения на которое производится ответ
	 * @return Message созданное сообщение
	 */
	public Message getNewMessage(String action, String destination, String origin, int inReplyTo){
	    return new StandartMessage(action, destination, origin, inReplyTo);
	}
	/** Установка статуса блокировки объекта по ресурсу */
	private void setBlockedState(String objName, boolean state){
	    if(!objects.containsKey(objName))
		return;
	    ((ObjectEntry)objects.get(objName)).blockedState = state;
	}
	/** Сброс записанных сообщений при снятии блокировки с объекта */
	private void flushDefferedMessages(String cl_n){
	    System.out.println("[D] flushing messages for "+cl_n);
	    if(!objects.containsKey(cl_n))
		return;
	    ObjectEntry oe = (ObjectEntry)objects.get(cl_n);
	    ODObject cl_send = oe.object;
	    cl_send.addMessages(messages.flush(cl_n));
	    synchronized(cl_send){cl_send.notify();}
	}
	/** Конструктор загружающий первоначальный набор объектов
	 * на основе списка
	 * @param objs список объектов для загрузки
	 */
	public StandartDispatcher(List objs){
	    System.err.println("[i] "+toString()+" starting up...");
	    StandartDispatcherHandler stdh = new StandartDispatcherHandler(new Integer(0));
	    stdh.start();
	    objects.put("stddispatcher", new ObjectEntry(getClass().getName(), false, stdh));
	    Iterator it = objs.iterator();

	    Pattern p = Pattern.compile("(o:|(r:)(\\d+:)?)(.+)");	    
	    while(it.hasNext()){
		int mult = 1;
		String cl_n = (String)it.next();
		Matcher m = p.matcher(cl_n);
		m.find();
		if(m.groupCount() == 4){
		    if(m.group(1).equals("o:"))
			loadObject(m.group(4));
		    if(m.group(1).startsWith("r:")){
			if(m.group(3) != null)
			    mult = new Integer(m.group(3).substring(0,m.group(3).length()-1)).intValue();
			loadResource(m.group(4), mult);
		    }
		}
	    }
	}
	/** Выводит сообщение об ошибке в случае некорректных параметров
	 */
	public static void usage(){
	    System.err.println("Usage: java com.novel.odisp.StandartDispatcher <file-with-list-of-ODobjects-to-load>");
	    System.exit(0);
	}
	/** Точка входа в StandartDispatcher.
	 * @param args по 0 должно содержать имя файла с перечислением классов, которые необходимо загрузить
	 */
	public static void main(String args[]){
	    if(args.length != 1)
		usage();
	    else {
		try {
		    BufferedReader cfg = new BufferedReader(new FileReader(args[0]));
		    List objs = new ArrayList();
		    String s;
		    while((s = cfg.readLine()) != null)
			if(!s.startsWith("#"))
			    objs.add(s);
		    new StandartDispatcher(objs);
		} catch (FileNotFoundException e){
		    System.err.println("[e] configuration file "+args[0]+" not found.");
		} catch (IOException e){
		    System.err.println("[e] unable to read configuration file.");
		}
	    }
	}

	/** Запись об объекте в таблице объектов */
	private class ObjectEntry {
	    public String className;
	    public boolean blockedState;
	    public ODObject object;
	    ObjectEntry(String cn, boolean bs, ODObject od){
		className = cn;
		blockedState = bs;
		object = od;
	    }
	}
	/** Коллекция отложенных сообщений */
	private class DefferedMessages {
	    Map queues = new HashMap();
	    public void addMessage(String objName, Message m){
		if(!queues.containsKey(objName)){
		    List messages = new ArrayList();
		    messages.add(m);
		    queues.put(objName, messages);
		} else 
		    ((List)queues.get(objName)).add(m);
	    }
	    public List flush(String objectName){
		if(queues.containsKey(objectName)){
		    List res = new ArrayList((List)queues.get(objectName));
		    queues.remove(objectName);
		    return res;
		} else
		    return new ArrayList();
	    }
	}
	/** Обработчик сообщений диспетчера */
	private class StandartDispatcherHandler extends CallbackODObject {
		public String name = "stddispatcher";
		protected void registerHandlers(){
		    addHandler("unload_object", new MessageHandler(){
			public void messageReceived(Message msg){
			    if(msg.getFieldsCount() != 1)
				return;
			    String name = (String)msg.getField(0);
			    unloadObject(name, 1);
			    objects.remove(name);
			}
		    });
		    addHandler("od_shutdown", new MessageHandler(){
			public void messageReceived(Message msg){
			    System.out.println("[i] "+toString()+" shutting down...");	    
			    Iterator it = objects.keySet().iterator();
			    while(it.hasNext())
			    unloadObject((String)it.next(), 0);
			    objects.clear();
			}
		    });
		    addHandler("od_acquire", new MessageHandler(){
			public void messageReceived(Message msg){
			    if(msg.getFieldsCount()>0){
				String cl_n = (String)msg.getField(0);
				boolean willBlockState = false;
				if(msg.getFieldsCount() == 2)
				    willBlockState = ((Boolean)msg.getField(1)).booleanValue();
				Iterator it = resources.keySet().iterator();
				while(it.hasNext()){ // first hit
				    String cur_cl_n = (String)it.next();
				    if(Pattern.matches(cl_n+":\\d+",cur_cl_n)){
					System.out.println("[D] od_acquire of resource "+cur_cl_n+" by "+msg.getOrigin());
					Message m = getNewMessage("resource_acquired", msg.getOrigin(), "stddispatcher", msg.getId());
					m.addField(cur_cl_n);
					m.addField(resources.get(cur_cl_n));
					resources.remove(cur_cl_n);
					sendMessage(m);
					setBlockedState(msg.getOrigin(), willBlockState);
					break;
				    }
				}
			    }
			}
		    });
		    addHandler("od_release", new MessageHandler(){
			public void messageReceived(Message msg){
			    if(msg.getFieldsCount() != 2)
				return;
			    String cl_n = (String)msg.getField(0);
			    Resource res = (Resource)msg.getField(1);
			    resources.put(cl_n, res);
			    flushDefferedMessages(msg.getOrigin());
			    setBlockedState(msg.getOrigin(), false);
			    System.out.println("[D] od_release of resource "+cl_n+" by "+msg.getOrigin());
			}
		    });
		}
		public int cleanUp(int type){
		    return 0;
		}
		public StandartDispatcherHandler(Integer id){
		    super("stddispatcher");
		}
	
	}
}