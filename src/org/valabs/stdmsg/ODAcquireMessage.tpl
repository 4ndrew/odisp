$Id: ODAcquireMessage.tpl,v 1.2 2004/07/12 12:31:00 valeks Exp $
NAME com.novel.stdmsg ODAcquireMessage od_acquire
AUTHOR <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
DESC Запрос диспетчеру на захват ресурса.
FIELD ResourceName String
FDESC ResourceName Имя ресурса для захвата
FCHECK ResourceName getResourceName(msg) != null
DEFDEST dispatcher