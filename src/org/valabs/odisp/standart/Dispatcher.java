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
 * @version $Id: Dispatcher.java,v 1.6 2003/10/12 14:27:50 valeks Exp $
 */
public class StandartDispatcher implements Dispatcher {
	Map objects = new HashMap();
	List messages = new ArrayList();
	int obj_count = 0;
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
		    objects.put(load.getObjectName(),load);
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
		ODObject obj = (ODObject)objects.get(objectName);
		Message m = getNewMessage("od_cleanup", objectName, "stddispatcher", 0);
		m.addField(new Integer(code));
		sendMessage(m);
		obj.interrupt();
		System.out.println("\tobject "+objectName+" unloaded");
		/* объект все еще может существовать, но сообщения ему доставлятся уже не будут */
	    }
	}
	/** Интерфейс для объектов ядра для отсылки сообщений.
	 * Реализует multicast рассылку сообщений
	 * @param message сообщение для отсылки
	 * @return void
	 */
	public void sendMessage(Message message){
	    Iterator it = objects.keySet().iterator();
	    while(it.hasNext()){
		String cl_n = (String)it.next();
		ODObject cl_send = (ODObject)objects.get(cl_n);
		cl_send.addMessage(message);
		System.out.println("[D] sending "+message+" to "+cl_n);
		synchronized(cl_send){cl_send.notify();}
	    }
//	    if(message.getDestination().equals(".*") || message.getDestination().equals("stddispatcher"))
//		handleMessage(message);
	}
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
		}
		public int cleanUp(int type){
		    return 0;
		}
		public StandartDispatcherHandler(Integer id){
		    super("stddispatcher");
		}
	
	}
	/** Конструктор загружающий первоначальный набор объектов
	 * на основе списка
	 * @param objs список объектов для загрузки
	 */
	public StandartDispatcher(List objs){
	    System.err.println("[i] "+toString()+" starting up...");
	    Iterator it = objs.iterator();
	    while(it.hasNext()){
		String cl_n = (String)it.next();
		loadObject(cl_n);
	    }
	    StandartDispatcherHandler stdh = new StandartDispatcherHandler(new Integer(0));
	    stdh.start();
	    objects.put("stddispatcher", stdh);
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
}