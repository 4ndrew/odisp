$Id: ODAddProviderMessage.tpl,v 1.1 2004/06/21 16:30:23 valeks Exp $
NAME com.novel.stdmsg ODAddProviderMessage od_add_provider
DESC Запрос на динамическое изменение списка сервисов
FIELD ServiceName String
FDESC ServiceName Имя сервиса
FCHECK ServiceName getServiceName(msg) != null
DEFDEST dispatcher
