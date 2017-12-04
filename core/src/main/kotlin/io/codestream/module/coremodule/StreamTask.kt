package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.yaml.YAMLStreamBuilder
import java.io.File
import javax.validation.constraints.NotBlank

class StreamTask : Task, TaskBinder {

    @NotBlank
    @TaskProperty
    var path:String = ""

    @TaskProperty(disableEvaluation = true)
    var inputParameters:Map<String, Any?> = mapOf();

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val stream = YAMLStreamBuilder(File(path)).build()
        val subCtx = ctx.subContext()
        return stream.run(inputParameters,subCtx)
    }
}