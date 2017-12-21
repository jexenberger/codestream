package io.codestream.module.utilmodule

import io.codestream.core.*
import io.codestream.resourcemodel.DefaultYamlResourceRegistry
import io.codestream.runtime.CodestreamRuntime
import io.codestream.runtime.StreamContext
import java.io.File
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Pattern

@TaskDescriptor("yaml-resources", description = "Loads a set of Resources from a Yaml configuration")
class YamlResourcesTask : Task, SetOutput {

    @TaskProperty(description = "Yaml file to load resources from")
    @NotEmpty
    var file: String = ""

    @TaskProperty(description = "Sets the loaded resource registry as a variable in the context, is required if scope is 'variable'")
    override var outputVar: String = ""


    @TaskProperty(description = "Scope to set the registry to. 'variable' sets it as a context variable, 'context' (default) sets it for the context, 'global' (server only) sets it for the whole runtime")
    @get:NotBlank
    @get:Pattern(regexp = "^(variable|context|global)")
    var scope: String = "context"

    @Synchronized
    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        if (!File(file).exists()) {
            return invalidParameter(id, "$file does not exist")
        }
        if ("variable".equals(scope) && outputVar.isBlank()) {
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
            ctx[outputVar] = registry
        }
        if ("context".equals(scope) || "global".equals(scope)) {
            ctx.resources = registry
        }
        if ("global".equals(scope)) {
            CodestreamRuntime.resourceRegistry = registry
        }
        return done()
    }


}