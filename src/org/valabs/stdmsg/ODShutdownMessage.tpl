$Id: ODShutdownMessage.tpl,v 1.3 2004/08/18 12:48:39 valeks Exp $
NAME com.novel.stdmsg ODShutdownMessage od_shutdown
DESC Класс реализующий сообщение диспетчеру ODISP об останове.
AUTHOR (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
DEFDEST dispatcher
FIELD ExitCode Integer
FDESC ExitCode Код выхода
FCHECK ExitCode true
