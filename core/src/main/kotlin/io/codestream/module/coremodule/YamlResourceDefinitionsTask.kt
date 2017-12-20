package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.resourcemodel.DefaultYamlResourceDefinitions
import io.codestream.runtime.StreamContext
import java.io.File
import javax.validation.constraints.NotEmpty

@TaskDescriptor("yaml-resource-definitions", description = "Loads a set of Resource definitions from a Yaml configuration")
class YamlResourceDefinitionsTask : Task {
    @TaskProperty(description = "Yaml file to load")
    @get:NotEmpty
    var file: String = ""

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        if (!File(file).exists()) {
            return invalidParameter(id, "$file does not exist")
        }
        val result = DefaultYamlResourceDefinitions(file).load()
        return result.right?.let { taskFailed(id, it.errors.joinToString()) }
    }
}