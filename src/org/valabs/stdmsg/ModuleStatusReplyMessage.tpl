$Id: ModuleStatusReplyMessage.tpl,v 1.1 2004/08/17 08:18:15 valeks Exp $
NAME com.novel.nms.messages ModuleStatusReplyMessage module_status_reply
AUTHOR <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
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