$Id: ModuleStatusReplyMessage.tpl,v 1.4 2005/09/28 13:30:35 valeks Exp $
NAME org.valabs.stdmsg ModuleStatusReplyMessage module_status_reply
AUTHOR <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
AUTHOR (C) 2004 ��� "�����-��"
DESC ����� �� ������ � ������� ������.
DESC ������ �� ������� �������� ��������� �������� ������� (�������,
DESC �����������, ���������). ������ �������� ����� ��������� �������
DESC �������� ������ �������, ������ ���������� �, ��������, ��������� ��������.
DEFROUTABLE 1
IMPORT java.util.List
FIELD RunningState String
FDESC RunningState ������� ��������� ������������� ������.
FIELD RunningTasks List
FDESC RunningTasks ������ ����� ����������� ������� ��������� ����� ����������� �������.
FCHECK RunningTasks true
FIELD CompletedTasks List
FDESC CompletedTasks ������ ������� ����������� �������.
FCHECK CompletedTasks true
FIELD FailedTasks List
FDESC FailedTasks ������ �� ����������� �����.
FCHECK FailedTasks true
