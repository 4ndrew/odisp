$Id: ModuleStatusReplyMessage.tpl,v 1.3 2004/08/23 07:42:36 valeks Exp $
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
FIELD CompletedTasks List
FDESC CompletedTasks ������ ������� ����������� �������.
FIELD FailedTasks List
FDESC FailedTasks ������ �� ����������� �����.
