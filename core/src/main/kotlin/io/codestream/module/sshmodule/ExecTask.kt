package io.codestream.module.sshmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import javax.validation.constraints.NotBlank

@TaskDescriptor("exec", description = "Executes a command against an SSH server")
class ExecTask : BaseSSHHandler(), Task {


    @TaskProperty(description = "Command to execute")
    @get:NotBlank
    var cmd: String = ""

    @TaskProperty(description = "Name of variable to set from exec output, default is '\$sshExec'")
    @get:NotBlank
    var outputVar: String = "\$sshExec"


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        return doInSession(id) {
            var buffer = ""
            it.exec(cmd) { line ->
                buffer += line + "\n"
            }
            ctx[outputVar] = buffer
            done()
        }
    }
}