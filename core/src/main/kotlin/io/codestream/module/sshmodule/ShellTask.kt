package io.codestream.module.sshmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.ssh.SSHSession
import javax.validation.constraints.NotBlank

@TaskDescriptor("shell", description = "Executes a command against an existing sshSession")
class ShellTask : Task, SetOutput {

    @TaskProperty(description = "Name of variable to set from Shell output, default is '\$sshShellOutput'")
    @get:NotBlank
    override var outputVar: String = "\$sshShellOutput"


    @TaskProperty(description = "Name of variable which holds SSH Session, default is '\$ssh'")
    @get:NotBlank
    var sessionVar: String = "\$ssh"

    @TaskProperty(description = "Command to execute on the shell")
    @get:NotBlank
    var cmd: String = ""


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val session: SSHSession = ctx[sessionVar] as SSHSession? ?: return invalidParameter(id, "$sessionVar is not an instance of ${SSHSession::class.qualifiedName}")
        return session.let {
            var buffer = ""
            it.shell(cmd, { buffer += it })
            ctx[outputVar] = buffer
            done()
        } ?: invalidParameter(id, "$sessionVar is not present in context")
    }
}