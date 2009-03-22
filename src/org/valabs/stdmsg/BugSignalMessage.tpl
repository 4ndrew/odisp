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
