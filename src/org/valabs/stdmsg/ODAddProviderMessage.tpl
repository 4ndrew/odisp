$Id: ODAddProviderMessage.tpl,v 1.2 2004/07/12 12:31:00 valeks Exp $
NAME com.novel.stdmsg ODAddProviderMessage od_add_provider
DESC ������ �� ������������ ��������� ������ ��������
AUTHOR <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
FIELD ServiceName String
FDESC ServiceName ��� �������
FCHECK ServiceName getServiceName(msg) != null
DEFDEST dispatcher
