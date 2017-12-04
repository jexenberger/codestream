package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.exec
import java.io.File
import java.util.concurrent.TimeUnit
import javax.validation.constraints.NotBlank

class ExecTask : Task, TaskBinder {

    @TaskProperty
    @get:NotBlank
    var cmd: String = ""

    @TaskProperty
    var dir: String = System.getProperty("user.dir")

    @TaskProperty
    var varName: String? = null

    @TaskProperty
    var timeout: Long = (60 * 60)

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val dirName = dir
        val pwdDir = File(dirName)
        if (!pwdDir.isDirectory) {
            return taskFailed(id, "$dirName does not exist or is not a directory")
        }
        val result = cmd.exec(dir = pwdDir, timeout = timeout, timeUnit = TimeUnit.SECONDS)
        if (result.first != 0) {
            return taskFailed(id, "$cmd returned with exit code ${result.first}")
        }
        varName?.let { ctx[it] = result.second }
        ctx.log(result.second)
        return done()
    }
}