package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

class ScriptTask : Task {

    @TaskProperty
    @get:NotBlank
    var script: String = ""

    @TaskProperty
    var outputVar: String? = null

    @TaskProperty
    @get:NotBlank
    @get:Pattern(regexp = "^(current|parent)")
    var scope: String = "current"


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val result = ctx.evalScript<Any?>(script)
        outputVar?.let {
            val workingCtx = if (scope.equals("parent")) ctx.parent?.let { it } ?: ctx else ctx
            workingCtx[it] = result
        }
        return done()
    }

}