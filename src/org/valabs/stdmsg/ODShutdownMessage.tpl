$Id: ODShutdownMessage.tpl,v 1.2 2004/07/12 12:31:00 valeks Exp $
NAME com.novel.stdmsg ODShutdownMessage od_shutdown
DESC Класс реализующий сообщение диспетчеру ODISP об останове.
AUTHOR <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
DEFDEST dispatcher
FIELD ExitCode Integer
FDESC ExitCode Код выхода
FCHECK ExitCode true
