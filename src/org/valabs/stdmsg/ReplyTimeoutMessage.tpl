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
$Id: ReplyTimeoutMessage.tpl,v 1.2 2005/09/30 14:21:47 dron Exp $
NAME org.valabs.stdmsg ReplyTimeoutMessage reply_timeout
DESC Сообщение для сигнализации о том, что сообщение которое было добавлено
DESC в SessionManager, за критическое время (указанное при добавлении) не получило
DESC ответа. Сообщение не содержит параметров; ReplyId соответствует оригинальному
DESC сообщению.
AUTHOR <a href="mailto:dron@valabs.spb.ru">Андрей А. Порохин</a>