package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import javax.validation.constraints.NotBlank

@TaskDescriptor("echo", description = "Displays a value to the console")
class EchoTask : Task {

    @TaskProperty(description = "Value to display")
    @get:NotBlank
    var value: String = ""

    @TaskProperty(description = "Indent value to display under Task banner, default is 'true'")
    var indent: Boolean = true

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        if (!indent) {
            ctx.echo(value)
            return done()
        }
        var buffer = ""
        (0..ctx.depthCnt + 1).forEach {
            buffer += "  "
        }
        ctx.echo("$buffer$value")
        return done()
    }
}