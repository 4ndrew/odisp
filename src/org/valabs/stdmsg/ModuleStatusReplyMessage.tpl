$Id: ModuleStatusReplyMessage.tpl,v 1.3 2004/08/23 07:42:36 valeks Exp $
NAME org.valabs.stdmsg ModuleStatusReplyMessage module_status_reply
AUTHOR <a href="mailto:valeks@novel-il.ru">Valentin A. Alekseev</a>
AUTHOR (C) 2004 НПП "Новел-ИЛ"
DESC Ответ на запрос о статусе модуля.
DESC Каждый из списков содержит строковые описания заданий (текущих,
DESC выполненных, ошибочных). Каждое описание может содержать краткое
DESC описание самого задания, стадию выполнения и, возможно, возникшие проблемы.
DEFROUTABLE 1
IMPORT java.util.List
FIELD RunningState String
FDESC RunningState Текущее состояние опрашиваемого модуля.
FIELD RunningTasks List
FDESC RunningTasks Список строк описывающих текущее состояние задач выполняемых модулем.
FIELD CompletedTasks List
FDESC CompletedTasks Список успешно выполненных заданий.
FIELD FailedTasks List
FDESC FailedTasks Список не выполненных задач.
