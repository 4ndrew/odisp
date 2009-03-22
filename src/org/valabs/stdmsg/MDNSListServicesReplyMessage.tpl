$Id: MDNSListServicesReplyMessage.tpl,v 1.1 2005/01/24 13:01:54 valeks Exp $
NAME org.valabs.stdmsg MDNSListServicesReplyMessage mdns_list_services
DESC Поиск сервиса в Rendezvous/Zeroconf.
IMPORT java.util.Set
FIELD Type String
FDESC Type Тип сервиса
FIELD Services Set
FDESC Services Список найденых сервисов.