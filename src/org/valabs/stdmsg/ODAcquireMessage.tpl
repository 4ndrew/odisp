$Id: ODAcquireMessage.tpl,v 1.4 2004/08/23 07:42:36 valeks Exp $
NAME org.valabs.stdmsg ODAcquireMessage od_acquire
AUTHOR (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
DESC ������ ���������� �� ������ �������.
FIELD ResourceName String
FDESC ResourceName ��� ������� ��� �������
FCHECK ResourceName getResourceName(msg) != null
DEFDEST dispatcher
