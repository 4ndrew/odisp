$Id: CorbaAddServiceMessage.tpl,v 1.1 2005/08/11 21:45:12 valeks Exp $
NAME org.valabs.stdmsg.corba CorbaAddServiceMessage corba_add_service
DESC ��������� ��� ���������� ������� � ��������.
AUTHOR <a href="mailto:valeks@valabs.spb.ru">�������� �������� �.</a>
IMPORT org.omg.PortableServer.Servant
FIELD ServiceName String
FDESC ServiceName ���������� ��� ������������ ��� ����������� ������� � ������� ����������
FIELD ServiceImpl Servant
FDESC ServiceImpl ������ �� �������� ����� ������������ ����������� ������� �������.
