$Id: ODAddProviderMessage.tpl,v 1.1 2004/06/21 16:30:23 valeks Exp $
NAME com.novel.stdmsg ODAddProviderMessage od_add_provider
DESC ������ �� ������������ ��������� ������ ��������
FIELD ServiceName String
FDESC ServiceName ��� �������
FCHECK ServiceName getServiceName(msg) != null
DEFDEST dispatcher
