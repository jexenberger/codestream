package io.codestream.util.log

class ConsoleLog : Log {

    val ANSI_RESET = "\u001B[39m"
    val ANSI_RED = "\u001B[31m"
    val ANSI_GREEN = "\u001B[32m"


    override fun log(entry: LogEntry) {

        when (entry.type) {
            LogType.DEBUG -> println(entry.entry)
            LogType.LOG -> println(entry.entry)
            LogType.INFO -> println(decorate(ANSI_GREEN, entry.entry, ANSI_RESET))
            LogType.ERROR -> {
                println(decorate(ANSI_RED, entry.entry, ANSI_RESET))
                entry.exceptionAsString()?.let {
                    println(decorate(ANSI_RED, it, ANSI_RESET))
                }

            }
        }

    }

    private fun decorate(pre: String, str: Any?, post: String): String {
        return "${pre}${str}${post}"
    }


}