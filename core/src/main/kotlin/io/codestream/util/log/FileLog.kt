package io.codestream.util.log

import java.io.File

class FileLog(val path: String) : Log {

    override var enableDebug: Boolean = false


    override fun log(entry: LogEntry) {
        File(path).appendText(entry.toOutputString())
    }
}