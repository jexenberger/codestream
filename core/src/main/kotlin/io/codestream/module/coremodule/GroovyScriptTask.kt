package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.Eval
import io.codestream.util.whenTrue
import javax.validation.constraints.NotBlank


@TaskDescriptor("groovy", description = "Runs a snippet of Groovy which evaluates against the context")
class GroovyScriptTask : Task, SetOutput {

    @TaskProperty(description = "Script to run")
    @get:NotBlank
    var script: String = ""

    @TaskProperty(description = "Name of output variable from result of script run to set")
    override var outputVar: String = ""

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val result = ctx.evalScript<Any?>(script, Eval.engineByName("nashorn"))
        outputVar.isNotEmpty().whenTrue {
            ctx[outputVar] = result
        }
        return done()
    }

}