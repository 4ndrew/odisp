$Id: CorbaAddServiceMessage.tpl,v 1.1 2005/08/11 21:45:12 valeks Exp $
NAME org.valabs.stdmsg.corba CorbaAddServiceMessage corba_add_service
DESC Сообщения для добавления сервера в менеджер.
AUTHOR <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>
IMPORT org.omg.PortableServer.Servant
FIELD ServiceName String
FDESC ServiceName Символьное имя используемое при регистрации сервиса в сервисе именования
FIELD ServiceImpl Servant
FDESC ServiceImpl Ссылка на реальный класс занимающийся реализацией данного сервиса.
