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
$Id: ODCleanupMessage.tpl,v 1.6 2005/01/26 08:22:53 valeks Exp $
NAME org.valabs.stdmsg ODCleanupMessage od_cleanup
DESC ��������� � ����������� ������ �������.
DESC @deprecated ���������� ������������ cleanUp(int). ��� ��������� ������ �� ����������.
AUTHOR (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
DEFORIGIN dispatcher
FIELD Reason Integer
FDESC Reason ������� ������
FCHECK Reason true
