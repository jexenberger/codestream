package io.codestream.module.iomodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import java.io.File
import java.nio.charset.Charset
import javax.validation.constraints.NotBlank

class ReadFileTask : Task, TaskBinder {

    @TaskProperty
    @get:NotBlank
    var src: String = ""

    @TaskProperty
    @get:NotBlank
    var outputVar: String = ""

    @TaskProperty
    @get:NotBlank
    var encoding: String = "UTF-8"

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val theFile = File(src)
        if (!theFile.exists()) {
            return invalidParameter(id, "$src does not exist")
        }
        if (theFile.isDirectory()) {
            return invalidParameter(id, "$src does is a directory")
        }
        if (!theFile.canRead()) {
            return invalidParameter(id, "$src does is not readable")
        }
        val text = theFile.readText(Charset.forName(encoding))
        ctx[outputVar] = text
        return done()
    }
}