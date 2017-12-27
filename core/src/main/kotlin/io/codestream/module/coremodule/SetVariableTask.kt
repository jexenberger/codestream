package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

@TaskDescriptor("set", description = "Sets a variable in the context")
class SetVariableTask : Task, SetOutput {


    @TaskProperty(description = "Value to set")
    var value: Any? = null

    @TaskProperty(description = "Name of the output variable to set")
    @get:NotBlank
    override var outputVar: String = ""

    @TaskProperty(description = "Type of the variable, default is 'string'")
    @get:NotNull
    var varType: String = "string"

    @TaskProperty(description = "Sets the scope set set the variable in either parent or current context, default is current context")
    @get:NotBlank
    @get:Pattern(regexp = "^(current|parent)")
    var scope: String = "current"


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val typeOfVar = Parameter.typeFor(varType)
        val valueToUse = ctx.evalTo<Any?>(value, typeOfVar)
        val workingCtx = if (scope.equals("parent")) ctx.parent?.let { it } ?: ctx else ctx
        workingCtx[outputVar] = valueToUse
        return done()
    }
}