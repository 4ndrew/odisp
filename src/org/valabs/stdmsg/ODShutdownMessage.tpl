$Id: ODShutdownMessage.tpl,v 1.4 2004/08/23 07:42:36 valeks Exp $
NAME org.valabs.stdmsg ODShutdownMessage od_shutdown
DESC Класс реализующий сообщение диспетчеру ODISP об останове.
AUTHOR (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
DEFDEST dispatcher
FIELD ExitCode Integer
FDESC ExitCode Код выхода
FCHECK ExitCode true
