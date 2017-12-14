package io.codestream.util.log

interface Log {
    fun debug(msg: Any?) = log(LogEntry(LogType.DEBUG, msg))
    fun log(msg: Any?) = log(LogEntry(LogType.INFO, msg))
    fun info(msg: Any?) = log(LogEntry(LogType.INFO, msg))
    fun error(msg: Any?, exception: Exception) = log(LogEntry(LogType.ERROR, msg, exception = exception))

    fun log(entry: LogEntry)

}