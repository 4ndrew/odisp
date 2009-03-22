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
$Id: WCAddServletMessage.tpl,v 1.1 2005/07/22 16:03:50 valeks Exp $
NAME org.valabs.stdmsg.webcon WCAddServletMessage wc_add_servlet
DESC Добавление сервлета в контейнер.
FIELD ServletMask String
FDESC ServletMask Маска запросов
FIELD ServletHandler Object
FDESC ServletHandler Ссылка на сервлет.