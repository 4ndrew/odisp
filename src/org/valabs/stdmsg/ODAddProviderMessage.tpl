$Id: ODAddProviderMessage.tpl,v 1.2 2004/07/12 12:31:00 valeks Exp $
NAME com.novel.stdmsg ODAddProviderMessage od_add_provider
DESC Запрос на динамическое изменение списка сервисов
AUTHOR <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
FIELD ServiceName String
FDESC ServiceName Имя сервиса
FCHECK ServiceName getServiceName(msg) != null
DEFDEST dispatcher
