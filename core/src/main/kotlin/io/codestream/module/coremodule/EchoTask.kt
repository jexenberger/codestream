package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext

@TaskDescriptor("echo", description = "Echos a cipherText to the console")
class EchoTask : Task {

    @TaskProperty(description = "Value to display")
    var value: Any? = "<EMPTY>"

    @TaskProperty(description = "Indent cipherText with spaces to display under banner, default is 'true'")
    var indent: Boolean = true

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        if (!indent) {
            ctx.echo(value?.toString() ?: "<EMPTY>")
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