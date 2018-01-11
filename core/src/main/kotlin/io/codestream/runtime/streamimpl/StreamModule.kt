package io.codestream.runtime.streamimpl

import io.codestream.core.*
import io.codestream.doc.ExecutableDocumentation
import io.codestream.doc.ParameterDocumentation
import java.io.File

class StreamModule(val path: String,
                   override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>> = mutableMapOf()) : Module {

    override val name: String

    override fun documentation(type: TaskType): ExecutableDocumentation? {
        return factories[type]?.let {

            val factory = it as StreamTaskFactory
            val stream = factory.stream
            val desc = stream.desc
            val parms = stream.parameters.mapValues {
                val parameter = it.value
                val parmType = Parameter.stringType(parameter.type) ?: throw IllegalStateException("${parameter.type} is unknown")
                ParameterDocumentation(it.key, parameter.desc, parmType, parameter.defaultValue?.toString())
            }.values.toTypedArray()
            ExecutableDocumentation(
                    name = name,
                    description = desc,
                    params = parms
            )
        }
    }

    override val description: String

    init {
        val file = File(path)
        if (!file.isDirectory) {
            throw IllegalStateException("$path is not a valid directory")
        }
        description = File(path, "description.txt").readText()
        name = file.name
        define {
            val streamsAsTasks = file.list { dir, name ->
                name.endsWith("yaml") || name.endsWith("yml") || name.endsWith("csy")
            }

            if (streamsAsTasks.isEmpty()) {
                throw IllegalStateException("$path is listed as stream module but no tasks are defined")
            }

            streamsAsTasks.forEach {
                val taskFile = File(path, it)
                val defn: Pair<String, Factory<Task>> = taskFile.nameWithoutExtension to (StreamTaskFactory(taskFile.absolutePath) as Factory<Task>)
                task(defn)
            }
        }
    }

}