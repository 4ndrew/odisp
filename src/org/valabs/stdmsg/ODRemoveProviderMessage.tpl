$Id$
NAME org.valabs.stdmsg ODRemoveProviderMessage od_remove_provider
DESC Запрос на динамическое изменение списка сервисов.
DESC @deprecated начиная с версии 1.17 StandartODObject'а нет возможности
DESC динамически изменить список сервисов объекта, который хранится для проверки
DESC совпадения destination сообщения и имени/сервисов предоставляемых объектом.
AUTHOR (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
DEFDEST dispatcher
FIELD ServiceName String
FDESC ServiceName Название сервиса.
