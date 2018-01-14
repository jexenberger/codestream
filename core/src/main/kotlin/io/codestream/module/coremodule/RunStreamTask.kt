package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.CodestreamRuntime
import io.codestream.runtime.StreamContext
import io.codestream.util.returnIfTrue
import java.io.File
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

@TaskDescriptor("stream", description = "Runs a stream file")
class RunStreamTask : Task {


    @TaskProperty(description = "name of the stream to run, will look for the file in 'pwd'")
    @get:NotBlank
    var name: String = ""

    @TaskProperty(description = "path of a file which contains a stream definition, if set, name will be ignored")
    var path: String = ""

    @TaskProperty(description = "Runs the stream in it's own isolated context, default is 'false'")
    var isolated: Boolean = false

    @TaskProperty(description = "Format of the stream file, currently only 'yaml' is supported which is the default")
    @get:NotBlank
    @get:Pattern(regexp = "^(yaml|builder)")
    var type: String = "yaml"

    @TaskProperty(description = "Map of input parameters which need to be set for the stream, if you are running a non isolated stream, you can omit parameters if they are already in the context")
    var inputParameters: MutableMap<String, Any?> = mutableMapOf()


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val file = path.isNotBlank().returnIfTrue { File(path) } ?: File(name)
        if (!file.exists()) {
            return invalidParameter(id, "${file.absolutePath} does not exist")
        }
        if (!type.equals("yaml")) {
            return invalidParameter(id, "${type} is not supported yet")
        }
        val runCtx = if (isolated) StreamContext(log = ctx.log) else ctx
        return CodestreamRuntime.runtime.runStream(
                ctx = runCtx,
                streamFile = file,
                inputParms = inputParameters,
                debug = ctx.log.debug
        )
    }
}