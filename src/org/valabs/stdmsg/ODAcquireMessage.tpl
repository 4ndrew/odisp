$Id: ODAcquireMessage.tpl,v 1.5 2004/08/30 10:07:12 valeks Exp $
NAME org.valabs.stdmsg ODAcquireMessage od_acquire
AUTHOR (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
DESC ������ ���������� �� ������ �������.
FIELD ResourceName String
FDESC ResourceName ��� ������� ��� �������
FCHECK ResourceName getResourceName(msg) != null
DEFDEST dispatcher
DEFOOB
