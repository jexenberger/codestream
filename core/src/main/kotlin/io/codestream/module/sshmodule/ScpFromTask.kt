package io.codestream.module.sshmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import java.io.File
import javax.validation.constraints.NotBlank

class ScpFromTask : BaseSSHHandler(), Task {

    @TaskProperty
    @NotBlank
    var src = ""

    @TaskProperty
    @NotBlank
    var target = ""

    @TaskProperty
    var overwrite = false

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        var file = File(target)
        if (file.isFile && !overwrite) {
            return invalidParameter(id, "$target already exists")
        }
        return doInSession {
            it.scpFrom(target, src)
            done()
        }
    }

}