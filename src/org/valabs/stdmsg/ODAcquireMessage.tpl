$Id: ODAcquireMessage.tpl,v 1.1 2004/06/21 16:30:23 valeks Exp $
NAME com.novel.stdmsg ODAcquireMessage od_acquire
DESC ������ ���������� �� ������ �������.
FIELD ResourceName String
FDESC ResourceName ��� ������� ��� �������
FCHECK ResourceName getResourceName(msg) != null
DEFDEST dispatcher