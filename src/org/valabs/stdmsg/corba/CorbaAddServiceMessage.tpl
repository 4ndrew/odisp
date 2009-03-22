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
$Id: CorbaAddServiceMessage.tpl,v 1.1 2005/08/11 21:45:12 valeks Exp $
NAME org.valabs.stdmsg.corba CorbaAddServiceMessage corba_add_service
DESC ��������� ��� ���������� ������� � ��������.
AUTHOR <a href="mailto:valeks@valabs.spb.ru">�������� �������� �.</a>
IMPORT org.omg.PortableServer.Servant
FIELD ServiceName String
FDESC ServiceName ���������� ��� ������������ ��� ����������� ������� � ������� ����������
FIELD ServiceImpl Servant
FDESC ServiceImpl ������ �� �������� ����� ������������ ����������� ������� �������.
