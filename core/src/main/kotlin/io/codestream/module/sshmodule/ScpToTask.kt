package io.codestream.module.sshmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import java.io.File
import javax.validation.constraints.NotBlank

class ScpToTask : BaseSSHHandler(), Task {

    @TaskProperty
    @NotBlank
    var src = ""

    @TaskProperty
    @NotBlank
    var target = ""


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val file = File(src)
        if (!file.exists()) {
            return invalidParameter(id, "$target does not exist")
        }
        return doInSession {
            it.scpTo(src, target)
            done()
        }
    }
}