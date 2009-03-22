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
$Id: ODShutdownMessage.tpl,v 1.5 2005/01/26 08:22:53 valeks Exp $
NAME org.valabs.stdmsg ODShutdownMessage od_shutdown
DESC ����� ����������� ��������� ���������� ODISP �� ��������.
AUTHOR (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
DEFDEST dispatcher
FIELD ExitCode Integer
FDESC ExitCode ��� ������
FCHECK ExitCode true
public static final int SHUTDOWN_RESTART = -1;