package io.codestream.util.log

import io.codestream.cli.Console

class ConsoleLog : Log {

    override var enableDebug: Boolean = false

    override fun log(entry: LogEntry) {

        when (entry.type) {
            LogType.DEBUG -> Console.line(entry.entry)
            LogType.LOG -> Console.line(entry.entry)
            LogType.INFO -> Console.green(entry.entry)
            LogType.ERROR -> {
                Console.red(entry.entry)
                entry.exceptionAsString()?.let {
                    Console.red(it)
                }

            }
        }

    }

}