$Id: BugSignalMessage.tpl,v 1.1 2005/01/11 20:37:59 valeks Exp $
NAME org.valabs.stdmsg BugSignalMessage bug_signal
AUTHOR (C) 2005 <a href="mailto:valeks@valabs.spb.ru">Алексеев Валентин А.</a>
DESC Регистрция сообщения об ошибке.
DEFDEST bugtrack
FIELD BugId String
FDESC BugId Код ошибки. Формат: [модуль]-[внутренний код ошибки].
FIELD BugPC String
FDESC BugPC Возможная причина ошибки.
FCHECK BugPC true
FIELD BugAI String
FDESC BugAI Дополнительная информация.
FCHECK BugAI true
