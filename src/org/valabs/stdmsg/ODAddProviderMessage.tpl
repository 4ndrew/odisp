$Id: ODAddProviderMessage.tpl,v 1.6 2005/03/03 08:47:50 valeks Exp $
NAME org.valabs.stdmsg ODAddProviderMessage od_add_provider
DESC Запрос на динамическое изменение списка сервисов.
DESC @deprecated начиная с версии 1.17 StandartODObject'а нет возможности
DESC динамически изменить список сервисов объекта, который хранится для проверки
DESC совпадения destination сообщения и имени/сервисов предоставляемых объектом.
AUTHOR (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
FIELD ServiceName String
FDESC ServiceName Имя сервиса
DEFDEST dispatcher
