package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

class SetVariableTask : Task, TaskBinder {


    @TaskProperty
    @get:NotNull
    var value: String = ""

    @TaskProperty
    @get:NotBlank
    var name: String = ""

    @TaskProperty
    @get:NotNull
    var varType: String = "string"

    @TaskProperty
    @get:NotBlank
    @get:Pattern(regexp = "^(current|parent)")
    var scope: String = "current"


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val typeOfVar = Parameter.typeFor(varType)
        val valueToUse = ctx.evalTo<Any?>(value, typeOfVar)
        val workingCtx = if (scope.equals("parent")) ctx.parent?.let { it } ?: ctx else ctx
        workingCtx[name] = valueToUse
        return done()
    }
}