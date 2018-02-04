package io.codestream.module.sshmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import java.io.File
import javax.validation.constraints.NotBlank

@TaskDescriptor("scp-from", description = "SCPs a file from a remote SSH server to the local filesystem")
class ScpFromTask : BaseSSHHandler(), Task {

    @TaskProperty(description = "Source file to copy")
    @get:NotBlank
    var src = ""

    @TaskProperty(description = "Target folder/name copy the file to")
    @get:NotBlank
    var target = ""

    @TaskProperty(description = "Overwrite existing target file if it already exists, default is 'false'")
    var overwrite = false

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val file = File(target)
        if (!file.exists() && !file.mkdirs()) {
            return taskFailed(id, "Attempted to create")
        }
        if (file.isFile && !overwrite) {
            return invalidParameter(id, "$target already exists and overwrite disabled")
        }
        return doInSession(id) {
            it.scpFrom(target, src)?.let { taskFailed(id, "Unable to scp -> $it") } ?: done()
        }
    }

}