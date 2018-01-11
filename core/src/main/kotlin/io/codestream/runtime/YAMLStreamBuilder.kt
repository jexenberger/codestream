package io.codestream.runtime

import io.codestream.core.*
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.events.ScalarEvent
import java.io.File
import java.io.FileInputStream
import java.io.StringReader


class YAMLStreamBuilder() {

    var data: Map<String, Any?> = mapOf()
    var yaml: String = ""
    private var idLineTracker: List<Pair<Int, Long>> = mutableListOf()
    private var idCnt = 0
    private val ids: Iterator<Pair<Int, Long>> by lazy { idLineTracker.iterator() }
    private var source: String = ""


    constructor(yaml: String) : this() {
        val yamlLoader = Yaml()
        this.yaml = yaml
        val events = yamlLoader.parse(StringReader(this.yaml))
        events.forEach {
            if (it is ScalarEvent && "task".equals(it.value)) {
                idLineTracker += idCnt++ to (it.startMark.line.toLong())
            }
        }
        @Suppress("UNCHECKED_CAST")
        this.data = yamlLoader.load(this.yaml) as Map<String, Any?>
    }

    constructor(streamFile: File) : this() {
        if (!streamFile.exists()) {
            throw IllegalArgumentException("${streamFile.name} does not exist")
        }
        if (streamFile.isDirectory) {
            throw IllegalArgumentException("${streamFile.name} is a directory")
        }
        this.source = streamFile.absolutePath
        val yamlLoader = Yaml()
        val events = yamlLoader.parse(FileInputStream(streamFile).reader())
        events.forEach {
            if (it is ScalarEvent && "task".equals(it.value)) {
                idLineTracker += idCnt++ to (it.startMark.line.toLong() + 1)
            }
        }
        @Suppress("UNCHECKED_CAST")
        this.data = yamlLoader.load(FileInputStream(streamFile)) as Map<String, Any?>

    }

    fun build(): Stream {
        val group = data["group"] as String? ?: "_default"
        val desc = data["desc"] as String? ?: ""
        val streamName = data["stream"] as String? ?: throw IllegalArgumentException("'stream' tag not defined")
        @Suppress("UNCHECKED_CAST")
        val inputs = data["inputs"] as List<Map<String, Any?>>? ?: listOf<Map<String, String?>>()
        val builder = StreamBuilder(streamName, group, desc)
        inputs.map { defineInput(it) }
                .forEach { builder.input(it) }
        @Suppress("UNCHECKED_CAST")
        val tasks = data["tasks"] as List<Map<String, Any?>>? ?: listOf<Map<String, String?>>()
        tasks.map {
            defineTask(group, streamName, builder, it)
        }
        return builder.stream
    }

    internal fun defineInput(taskMap: Map<String, Any?>): Parameter {
        val id = taskMap["input"] as String
        val desc = taskMap["prompt"] as String
        val type = taskMap["type"] as String? ?: "string"
        val default = taskMap["default"] as String?
        val required = taskMap["required"] as Boolean? ?: true
        val allowedValues = (taskMap["allowedValues"] as String?)?.let { it } ?: ""
        return Parameter(id, desc, type, default, required, allowedValues)
    }

    internal fun defineTask(group: String, stream: String, builder: StreamBuilder, taskMap: Map<String, Any?>) {
        val task = taskMap["task"]
        val type = TaskType.fromString(required(task, "task", null))

        //this id line logic works because the map returned is a linked HashMap so the tasks occur in order
        //this means we can assign line numbers in order of appearance
        val (idCnt, taskLine) = ids.next()
        val id = taskMap["id"]?.toString() ?: "${type.fqn}-$idCnt"
        val taskId = TaskId(group, stream, type, id)
        val condition = taskMap["condition"] as String?

        val parms = taskMap
                .filterKeys { !arrayOf("id", "task", "type", "tasks", "condition").contains(it) }
                .filterValues { it != null }
        val binding = type.module?.binding<Executable>(type, parms) ?: throw IllegalArgumentException("$task does not exist either module or task does not exist")

        val defn = ExecutableDefinition(type = type,
                id = taskId,
                condition = condition?.let { scriptCondition(it) } ?: defaultCondition(),
                binding = binding,
                lineNumber = taskLine,
                source = source
        )
        if (taskMap.containsKey("tasks")) {
            @Suppress("UNCHECKED_CAST")
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
        @Suppress("UNCHECKED_CAST")
        return value as Z
    }


}