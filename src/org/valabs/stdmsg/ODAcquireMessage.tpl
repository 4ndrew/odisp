$Id: ODAcquireMessage.tpl,v 1.3 2004/08/18 12:48:39 valeks Exp $
NAME com.novel.stdmsg ODAcquireMessage od_acquire
AUTHOR (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
DESC Запрос диспетчеру на захват ресурса.
FIELD ResourceName String
FDESC ResourceName Имя ресурса для захвата
FCHECK ResourceName getResourceName(msg) != null
DEFDEST dispatcher