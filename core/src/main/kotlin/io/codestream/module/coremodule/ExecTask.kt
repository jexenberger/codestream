package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.exec
import io.codestream.util.system
import io.codestream.util.whenTrue
import java.io.File
import java.util.concurrent.TimeUnit
import javax.validation.constraints.NotBlank

@TaskDescriptor("exec", description = "Runs a command against the OS")
class ExecTask : Task, SetOutput {

    @TaskProperty(description = "Command to run")
    @get:NotBlank
    var cmd: String = ""

    @TaskProperty(description = "Directory to run the command in, default is 'pwd'")
    var dir: String = system.pwd

    @TaskProperty(description = "Output variable to set from the command output, default is '\$exec'")
    override var outputVar: String = "\$exec"

    @TaskProperty(description = "Timeout in seconds, default is 1 hour")
    var timeout: Long = (60 * 60)

    @TaskProperty(description = "Echo cmd output to Console, default is false")
    var echo: Boolean = false

    @TaskProperty(description = "Fail the task if the command fails with exist status <> 0, default is 'false'")
    var exitOnFail = false

    @TaskProperty(description = "Output variable to set the command exit status to, default is '\$exitStatus'")
    @get:NotBlank
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
                ctx.echo(it)
            }
        }
        ctx[exitStatus] = result
        outputVar.isNotBlank().whenTrue {
            ctx[outputVar] = buffer
        }
        if (result != 0 && exitOnFail) {
            return taskFailed(id, "$cmd returned with exit code ${result}")
        }
        return done()
    }
}