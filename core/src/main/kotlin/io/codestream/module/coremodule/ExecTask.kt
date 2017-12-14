package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.exec
import io.codestream.util.system
import java.io.File
import java.util.concurrent.TimeUnit
import javax.validation.constraints.NotBlank

class ExecTask : Task, TaskBinder {

    @TaskProperty
    @get:NotBlank
    var cmd: String = ""

    @TaskProperty
    var dir: String = system.pwd

    @TaskProperty
    var varName: String? = null

    @TaskProperty
    var timeout: Long = (60 * 60)

    @TaskProperty
    var echo: Boolean = false

    @TaskProperty
    var exitOnFail = false

    @TaskProperty
    var exitStatus = "\$exitStatus"

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val dirName = dir
        val pwdDir = File(dirName)
        if (!pwdDir.isDirectory) {
            return taskFailed(id, "$dirName does not exist or is not a directory")
        }
        var buffer = ""
        val result = cmd.exec(dir = pwdDir, timeout = timeout, timeUnit = TimeUnit.SECONDS) {
            buffer += "$it\n"
            if (echo) {
                ctx.log(it)
            }
        }
        ctx[exitStatus] = result
        varName?.let { ctx[it] = buffer }
        if (result != 0 && exitOnFail) {
            return taskFailed(id, "$cmd returned with exit code ${result}")
        }
        return done()
    }
}