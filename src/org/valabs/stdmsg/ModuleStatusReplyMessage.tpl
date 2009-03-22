/* ODISP -- Message Oriented Middleware
 * Copyright (C) 2003-2005 Valentin A. Alekseev
 * Copyright (C) 2003-2005 Andrew A. Porohin 
 * 
 * ODISP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.1 of the License.
 * 
 * ODISP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ODISP.  If not, see <http://www.gnu.org/licenses/>.
 */
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
