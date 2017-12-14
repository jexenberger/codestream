package io.codestream.util.log

import java.io.PrintWriter
import java.io.StringWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class LogType {
    DEBUG,
    INFO,
    LOG,
    ERROR
}

data class LogEntry(val type: LogType,
                    val entry: Any?,
                    val timestamp: LocalDateTime = LocalDateTime.now(),
                    val exception: Exception? = null) {


    fun toOutputString(): String {
        val baseMessage = "${timestamp.format(DateTimeFormatter.ISO_DATE_TIME)}::${type}::["
        val content = exceptionAsString()?.let {
            "${entry?.toString()}\n${it}"
        } ?: entry?.toString()
        return "$baseMessage$content]"
    }

    internal fun exceptionAsString(): String? {
        return exception?.let {
            val output = StringWriter()
            it.printStackTrace(PrintWriter(output))
            output.buffer.toString()
        }
    }

}


