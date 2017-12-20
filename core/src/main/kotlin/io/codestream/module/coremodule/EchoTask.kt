package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import javax.validation.constraints.NotBlank


class EchoTask : Task {

    @TaskProperty
    @NotBlank
    var value: String = ""

    @TaskProperty
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