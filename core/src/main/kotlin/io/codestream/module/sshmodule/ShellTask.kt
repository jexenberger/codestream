package io.codestream.module.sshmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.ssh.SSHSession
import javax.validation.constraints.NotBlank

class ShellTask : Task, SetOutput {

    @TaskProperty
    @NotBlank
    override var outputVar: String = "\$sshShellOutput"


    @TaskProperty
    var sessionVar: String = "\$ssh"

    @TaskProperty
    @NotBlank
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