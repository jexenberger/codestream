package io.codestream.module.sshmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import javax.validation.constraints.NotBlank

class ExecTask : BaseSSHHandler(), Task {


    @TaskProperty
    @NotBlank
    var cmd: String = ""

    var outputVar: String = "\$sshExecOutputVar"


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        doInSession {
            var buffer = ""
            it.exec(cmd) { line ->
                buffer += line
            }
            ctx[outputVar] = buffer
            done()
        }
        return done()
    }
}