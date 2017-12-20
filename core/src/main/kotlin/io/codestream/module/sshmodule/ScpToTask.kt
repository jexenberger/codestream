package io.codestream.module.sshmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import java.io.File
import javax.validation.constraints.NotBlank

@TaskDescriptor("scp-to", description = "SCPs a remote file to the local filesystem")
class ScpToTask : BaseSSHHandler(), Task {

    @TaskProperty(description = "Source file to copy from remote SSH server")
    @get:NotBlank
    var src = ""

    @TaskProperty(description = "Target path to copy file to on local filesystem")
    @get:NotBlank
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