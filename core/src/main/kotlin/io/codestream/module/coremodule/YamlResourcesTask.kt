package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.resourcemodel.DefaultYamlResourceRegistry
import io.codestream.runtime.CodestreamRuntime
import io.codestream.runtime.StreamContext
import java.io.File
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Pattern

class YamlResourcesTask : Task {

    @TaskProperty
    @NotEmpty
    var file: String = ""

    @TaskProperty
    var varName: String = ""

    @get:NotBlank
    @get:Pattern(regexp = "^(variable|stream|global)")
    var scope: String = "stream"

    @Synchronized
    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        if (!File(file).exists()) {
            return invalidParameter(id, "$file does not exist")
        }
        if ("variable".equals(scope) && varName.isBlank()) {
            return invalidParameter(id, "scope is 'variable' but no variable name defined")
        }
        val registry = DefaultYamlResourceRegistry(file)
        val result = registry.load()
        return result?.let {
            taskFailed(id, it.message ?: "", it.errors.map { TaskError(id, "ResourceTaskFailed", it) }.toTypedArray())
        } ?: populateCtx(ctx, registry)
    }

    fun populateCtx(ctx: StreamContext, registry: DefaultYamlResourceRegistry): TaskError? {
        if ("variable".equals(scope)) {
            ctx[varName] = registry
        }
        if ("stream".equals(scope) || "global".equals(scope)) {
            ctx.resources = registry
        }
        if ("global".equals(scope)) {
            CodestreamRuntime.resourceRegistry = registry
        }
        return done()
    }


}