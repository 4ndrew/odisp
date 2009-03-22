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
$Id: ODAddProviderMessage.tpl,v 1.6 2005/03/03 08:47:50 valeks Exp $
NAME org.valabs.stdmsg ODAddProviderMessage od_add_provider
DESC ������ �� ������������ ��������� ������ ��������.
DESC @deprecated ������� � ������ 1.17 StandartODObject'� ��� �����������
DESC ����������� �������� ������ �������� �������, ������� �������� ��� ��������
DESC ���������� destination ��������� � �����/�������� ��������������� ��������.
AUTHOR (C) 2004 <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
FIELD ServiceName String
FDESC ServiceName ��� �������
DEFDEST dispatcher
