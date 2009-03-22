$Id: ODShutdownMessage.tpl,v 1.5 2005/01/26 08:22:53 valeks Exp $
NAME org.valabs.stdmsg ODShutdownMessage od_shutdown
DESC Класс реализующий сообщение диспетчеру ODISP об останове.
AUTHOR (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
DEFDEST dispatcher
FIELD ExitCode Integer
FDESC ExitCode Код выхода
FCHECK ExitCode true
public static final int SHUTDOWN_RESTART = -1;