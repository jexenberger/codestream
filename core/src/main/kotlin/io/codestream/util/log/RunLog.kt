package io.codestream.util.log

import io.codestream.core.TaskId

class RunLog(val runLog: Log, val displayLog: Log = ConsoleLog()) {

    var debug: Boolean
        get() = runLog.enableDebug
        set(value) {
            runLog.enableDebug = value
            displayLog.enableDebug = value
        }

    fun echo(msg: Any?) {
        displayLog.info(msg)
    }

    fun info(id: TaskId, msg: Any?) {
        runLog.info("$id => $msg")
    }

    fun error(id: TaskId, msg: Any?, e: Exception? = null) {
        val entry = "$id => $msg"
        e?.let { runLog.error(entry, e) } ?: runLog.error(entry)
        e?.let { displayLog.error(entry, e) } ?: displayLog.error(entry)

    }

    fun debug(id: TaskId, msg: Any?) {
        if (debug) {
            displayLog.debug("$id => $msg")
        }
        runLog.debug("$id => $msg")
    }
}