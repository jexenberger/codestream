package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.Eval
import io.codestream.util.whenTrue
import javax.validation.constraints.NotBlank

@TaskDescriptor("script", description = "Runs a snippet of code in the define script engine")
open class ScriptTask : Task, SetOutput {


    @TaskProperty(description = "Engine to run")
    var engine: String = "groovy"

    @TaskProperty(description = "Script to run")
    @get:NotBlank
    var script: String = ""

    @TaskProperty(description = "Name of output variable from result of script run to set")
    override var outputVar: String = ""

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val result = ctx.evalScript<Any?>(script, Eval.engineByName(engine))
        outputVar.isNotEmpty().whenTrue {
            ctx[outputVar] = result
        }
        return done()
    }
}