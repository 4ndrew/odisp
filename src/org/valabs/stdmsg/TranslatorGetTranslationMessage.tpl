$Id: TranslatorGetTranslationMessage.tpl,v 1.2 2004/08/23 07:42:36 valeks Exp $
NAME org.valabs.stdmsg TranslatorGetTranslationMessage translator_get_translation
AUTHOR (C) 2004 НПП "Новел-ИЛ"
AUTHOR Валентин А. Алексеев
DESC Запрос на получение строк трансляции для указанного языка.
DEFROUTABLE 1
FIELD Language String
FDESC Language Язык необходимый для трансляции.
FIELD Encoding String
FDESC Encoding Вариант языка.
FCHECK Encoding true