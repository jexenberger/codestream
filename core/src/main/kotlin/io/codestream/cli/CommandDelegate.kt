package io.codestream.cli

import io.codestream.core.TaskError
import io.codestream.core.TaskType
import io.codestream.runtime.CodestreamRuntime
import io.codestream.runtime.StreamContext
import io.codestream.util.log.Log
import java.io.File

fun run(log: Log, runtime: CodestreamRuntime, stream: String?, parms: Map<String, Any?>, debug: Boolean) {
    if (stream.isNullOrBlank()) {
        log.error("Valid stream name is a required parameter")
        return
    }

    val result = runtime.runStream(File(stream), debug = debug, inputParms = parms.toMap(), inputResolver = { _, parm ->
        var done: Boolean
        var input: String?
        val isRequired = parm.required
        val default = parm.defaultValue
        val prompt = "${parm.desc} ${if (isRequired) "(required)" else ""}  ${if (default != null) "(press enter to default to '$default')" else ""} :"
        do {
            print(prompt)
            input = readLine()?.trim() ?: default?.toString()
            input = if (input.isNullOrEmpty() && default != null) default.toString() else input
            done = !(isRequired && input.isNullOrEmpty())
        } while (!done)
        input!!
    })
    displayError(result, log)
}

private fun displayError(result: TaskError?, log: Log) {
    result?.let { errors ->
        log.error(errors.msg)
        errors.errors.forEach { log.error(it.msg) }
    }
}

fun task(log: Log, runtime: CodestreamRuntime, task: String?, parms: Map<String, Any?>, debug: Boolean) {
    if (task.isNullOrBlank()) {
        log.error("Valid task type is a required parameter")
        return
    }
    val ctx = StreamContext()
    val taskType = TaskType.fromString(task!!)
    val result = runtime.runTask(taskType, parms.toMap(), ctx, debug = debug)
    displayError(result, log)
    ctx.forEach { (k, v) -> log.info("[$k]=$v") }

}

fun help(log: Log, runtime: CodestreamRuntime, param: String?, parms: Map<String, Any?>, debug: Boolean) {
    param?.let {
        if (it.contains("::")) {
            displayTaskHelp(it.trim())
        } else {
            displayModuleHelp(it.trim())
        }
    } ?: displayModules()
}