$Id: ODAcquireMessage.tpl,v 1.6 2004/10/28 22:53:16 valeks Exp $
NAME org.valabs.stdmsg ODAcquireMessage od_acquire
AUTHOR (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
DESC Запрос диспетчеру на захват ресурса.
DESC @deprecated Необходимо переходить на новую схему работы с ресурсами
FIELD ResourceName String
FDESC ResourceName Имя ресурса для захвата
FCHECK ResourceName getResourceName(msg) != null
DEFDEST dispatcher
DEFOOB
