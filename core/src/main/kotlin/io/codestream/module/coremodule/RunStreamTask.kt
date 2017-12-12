package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.CodestreamRuntime
import io.codestream.runtime.StreamContext
import java.io.File
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

class RunStreamTask : Task, TaskBinder {


    @TaskProperty()
    @get:NotBlank
    var name: String = ""

    @TaskProperty
    var path: String? = ""

    @TaskProperty
    var isolated: Boolean = true

    @TaskProperty
    @get:NotBlank
    @get:Pattern(regexp = "^(yaml|builder)")
    var type: String = "yaml"

    @TaskProperty
    var input: Map<String, Any?> = mapOf()


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val file = path?.let { File(path) } ?: File(name)
        if (!file.exists()) {
            return invalidParameter(id, "${file.absolutePath} does not exist")
        }
        if (!type.equals("yaml")) {
            return invalidParameter(id, "${type} is not supported yet")
        }
        val runCtx = if (isolated) StreamContext() else ctx
        return CodestreamRuntime.runtime.runStream(
                ctx = runCtx,
                streamFile = file,
                inputParms = input
        )
    }
}