$Id: BugSignalMessage.tpl,v 1.1 2005/01/11 20:37:59 valeks Exp $
NAME org.valabs.stdmsg BugSignalMessage bug_signal
AUTHOR (C) 2005 <a href="mailto:valeks@valabs.spb.ru">�������� �������� �.</a>
DESC ���������� ��������� �� ������.
DEFDEST bugtrack
FIELD BugId String
FDESC BugId ��� ������. ������: [������]-[���������� ��� ������].
FIELD BugPC String
FDESC BugPC ��������� ������� ������.
FCHECK BugPC true
FIELD BugAI String
FDESC BugAI �������������� ����������.
FCHECK BugAI true
