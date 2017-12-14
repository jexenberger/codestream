package io.codestream.util.log

import io.codestream.core.TaskId

class RunLog(val runLog: Log, val displayLog: Log = ConsoleLog()) {

    fun echo(msg: Any?) {
        displayLog.info(msg)
    }

    fun info(id: TaskId, msg: Any?) {

    }

    private fun buildMessage(id: TaskId, msg: Any?): String {
        return "$id"
    }


}