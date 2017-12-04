package io.codestream.yaml

import io.codestream.core.*
import io.codestream.runtime.Stream
import io.codestream.runtime.StreamBuilder
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.util.*


class YAMLStreamBuilder() {

    var data: Map<String, Any?> = mapOf()
    var yaml: String = ""


    constructor(yaml: String) : this() {
        this.yaml = yaml
        this.data = Yaml().load(this.yaml) as Map<String, Any?>
    }

    constructor(streamFile: File) : this() {
        if (!streamFile.exists()) {
            throw IllegalArgumentException("${streamFile.name} does not exist")
        }
        if (streamFile.isDirectory()) {
            throw IllegalArgumentException("${streamFile.name} is a directory")
        }
        this.data = Yaml().load(FileInputStream(streamFile)) as Map<String, Any?>
    }

    fun build(): Stream {
        val group = data["group"] as String? ?: "_default"
        val desc = data["desc"] as String? ?: ""
        val streamName = data["stream"] as String? ?: throw IllegalArgumentException("'stream' tag not defined")
        val inputs = data["inputs"] as List<Map<String, Any?>>? ?: listOf<Map<String, String?>>()
        val builder = StreamBuilder(streamName, group, desc)
        for (input in inputs) {
            val inputType = defineInput(input)
            builder.input(inputType)
        }
        val tasks = data["tasks"] as List<Map<String, Any?>>? ?: listOf<Map<String, String?>>()
        tasks.map {
            defineTask(group, streamName, builder, it)
        }
        return builder.stream
    }

    fun defineInput(taskMap: Map<String, Any?>): Parameter {
        val id = taskMap["input"] as String
        val desc = taskMap["prompt"] as String
        val type = taskMap["type"] as String
        val default = taskMap["default"] as String?
        val required = taskMap["required"] as Boolean? ?: true
        val allowedValues = (taskMap["allowedValues"] as String?)?.let { it } ?: ""
        return Parameter(id, desc, type, default, required, allowedValues)
    }

    fun defineTask(group: String, stream: String, builder: StreamBuilder, taskMap: Map<String, Any?>) {
        val type = TaskType.fromString(required(taskMap["task"], "task", null))
        val id = taskMap["id"]?.toString() ?: "${type.fqn}-${UUID.randomUUID().toString()}"
        val taskId = TaskId(group, stream, id)
        val condition = taskMap["condition"] as String?

        val parms = taskMap
                .filterKeys { !arrayOf("id", "task", "type", "tasks", "condition").contains(it) }
                .filterValues { it != null }

        val defn = ExecutableDefinition(type, taskId, condition?.let {  scriptCondition(it) } ?: defaultCondition(), parms)
        if (taskMap.containsKey("tasks")) {
            val subTasks = taskMap["tasks"] as List<Map<String, Any?>>
            builder.task(defn) {
                subTasks.forEach { theTask ->
                    defineTask(group, stream, builder, theTask)
                }
            }
        } else {
            builder.task(defn)
        }
    }

    fun <Z> required(value: Any?, field: String, id: TaskId?): Z {
        value ?: throw ParseError("RequiredField", "$field is required for task ${id?.let { id.fqid } ?: ""}")
        return value as Z
    }


}