$Id: ODAcquireMessage.tpl,v 1.6 2004/10/28 22:53:16 valeks Exp $
NAME org.valabs.stdmsg ODAcquireMessage od_acquire
AUTHOR (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
DESC ������ ���������� �� ������ �������.
DESC @deprecated ���������� ���������� �� ����� ����� ������ � ���������
FIELD ResourceName String
FDESC ResourceName ��� ������� ��� �������
FCHECK ResourceName getResourceName(msg) != null
DEFDEST dispatcher
DEFOOB
