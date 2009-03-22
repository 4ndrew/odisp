$Id: ODCleanupMessage.tpl,v 1.6 2005/01/26 08:22:53 valeks Exp $
NAME org.valabs.stdmsg ODCleanupMessage od_cleanup
DESC Сообщение о прекращении работы объекта.
DESC @deprecated Необходимо использовать cleanUp(int). Это сообщение больше не посылается.
AUTHOR (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
DEFORIGIN dispatcher
FIELD Reason Integer
FDESC Reason Причина выхода
FCHECK Reason true
