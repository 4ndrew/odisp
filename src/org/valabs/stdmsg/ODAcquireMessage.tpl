$Id: ODAcquireMessage.tpl,v 1.1 2004/06/21 16:30:23 valeks Exp $
NAME com.novel.stdmsg ODAcquireMessage od_acquire
DESC Запрос диспетчеру на захват ресурса.
FIELD ResourceName String
FDESC ResourceName Имя ресурса для захвата
FCHECK ResourceName getResourceName(msg) != null
DEFDEST dispatcher