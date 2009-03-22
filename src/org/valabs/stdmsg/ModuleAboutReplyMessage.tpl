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
$Id: ModuleAboutReplyMessage.tpl,v 1.4 2004/08/23 07:42:36 valeks Exp $
NAME org.valabs.stdmsg ModuleAboutReplyMessage module_about_reply
AUTHOR <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
AUTHOR (C) 2004 НПП "Новел-ИЛ"
DESC Ответ на запрос об описании объекта.
DEFROUTABLE 1
FIELD Name String
FDESC Name Название модуля.
FIELD Version String
FDESC Version Версия модуля.
FIELD Copyright String
FDESC Copyright Авторские права на модуль.
FIELD AI String
FDESC AI Дополнительная информация.
FCHECK AI true
