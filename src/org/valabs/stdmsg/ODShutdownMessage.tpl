$Id: ODShutdownMessage.tpl,v 1.1 2004/06/21 17:09:25 valeks Exp $
NAME com.novel.stdmsg ODShutdownMessage od_shutdown
DESC Класс реализующий сообщение диспетчеру ODISP об останове.
DEFDEST dispatcher
FIELD ExitCode Integer
FDESC ExitCode Код выхода
FCHECK ExitCode true
