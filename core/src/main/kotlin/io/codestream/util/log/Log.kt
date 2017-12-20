package io.codestream.util.log

interface Log {

    var enableDebug: Boolean

    fun debug(msg: Any?) = if (enableDebug) {
        log(LogEntry(LogType.DEBUG, msg))
    } else Unit
    fun log(msg: Any?) = log(LogEntry(LogType.INFO, msg))
    fun info(msg: Any?) = log(LogEntry(LogType.INFO, msg))
    fun error(msg: Any?) = log(LogEntry(LogType.ERROR, msg))
    fun error(msg: Any?, exception: Exception) = log(LogEntry(LogType.ERROR, msg, exception = exception))

    fun log(entry: LogEntry)

}