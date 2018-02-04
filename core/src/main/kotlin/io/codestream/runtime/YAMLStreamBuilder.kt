package io.codestream.runtime

import io.codestream.core.*
import io.codestream.util.log.ConsoleLog
import io.codestream.util.log.Log
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.events.Event
import org.yaml.snakeyaml.events.MappingStartEvent
import org.yaml.snakeyaml.events.ScalarEvent
import java.io.File
import java.io.FileInputStream
import java.io.Reader
import java.io.StringReader


class YAMLStreamBuilder(val echo: Boolean = true, val log: Log = ConsoleLog()) {

    var data: Map<String, Any?> = mapOf()
    var yaml: String = ""
    private var idLineTracker: List<Pair<Int, Long>> = mutableListOf()
    private var idCnt = 0
    private val ids: Iterator<Pair<Int, Long>> by lazy { idLineTracker.iterator() }
    private var source: String = ""
    private var defaultGroup = "_default"


    constructor(yaml: String, echo: Boolean = false, log: Log = ConsoleLog()) : this(echo, log) {
        this.yaml = yaml
        trackLines(StringReader(yaml))
        @Suppress("UNCHECKED_CAST")
        this.data = Yaml().load(this.yaml) as Map<String, Any?>
    }

    constructor(streamFile: File, echo: Boolean = false, log: Log = ConsoleLog()) : this(echo, log) {
        if (!streamFile.exists()) {
            throw IllegalArgumentException("${streamFile.name} does not exist")
        }
        if (streamFile.isDirectory) {
            throw IllegalArgumentException("${streamFile.name} is a directory")
        }
        this.defaultGroup = if (streamFile.absoluteFile.parentFile != null) streamFile.absoluteFile.parentFile.name else "_default"
        this.source = streamFile.absolutePath
        trackLines(streamFile.reader())
        @Suppress("UNCHECKED_CAST")
        this.data = Yaml().load(FileInputStream(streamFile)) as Map<String, Any?>

    }

    private fun trackLines(source: Reader) {
        val events = Yaml().parse(source)
        var beginTracking = false
        var frame1: Event?
        var frame2: Event? = null
        var frame3: Event? = null
        events.forEach {
            frame1 = frame2
            frame2 = frame3
            frame3 = it
            if (it is ScalarEvent && "inputs".equals(it.value)) {
                beginTracking = false
            }
            if (it is ScalarEvent && "tasks".equals(it.value)) {
                beginTracking = true
            }

            if (beginTracking && frame1 is MappingStartEvent && frame2 is ScalarEvent && frame3 is MappingStartEvent) {
                idLineTracker += idCnt++ to (it.startMark.line.toLong() + 1)
            }
        }
    }

    fun build(): Stream {
        val group = data["group"] as String? ?: this.defaultGroup
        val streamName = data["stream"] as String? ?: throw IllegalArgumentException("'stream' tag is required")
        val desc = data["desc"] as String? ?: throw IllegalArgumentException("'desc' tag is required")
        @Suppress("UNCHECKED_CAST")
        val inputs = data["inputs"] as List<Map<String, Any?>>? ?: listOf<Map<String, String?>>()
        val builder = StreamBuilder(streamName, group, desc, echo = this.echo)
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
        val idKey = taskMap.keys.iterator().next()
        log.debug("Defining $idKey")
        @Suppress("UNCHECKED_CAST")
        val inputMap = taskMap[idKey] as Map<String, Any?>
        val id = idKey
        val desc = inputMap["prompt"] as String
        val type = inputMap["type"] as String? ?: "string"
        val default = inputMap["default"]
        val required = inputMap["required"] as Boolean? ?: true
        val allowedValues = (inputMap["allowedValues"] as String?)?.let { it } ?: ""
        return Parameter(id, desc, type, default?.toString(), required, allowedValues)
    }

    internal fun defineTask(group: String, stream: String, builder: StreamBuilder, taskMap: Map<String, Any?>) {
        val idKey = taskMap.keys.iterator().next()
        @Suppress("UNCHECKED_CAST")
        val parmMap = taskMap[idKey] as Map<String, Any?>
        val task = idKey
        val type = TaskType.fromString(required(task, "task", null))
        log.debug("Defining ${type}")

        //this id line logic works because the map returned is a linked HashMap so the tasks occur in order
        //this means we can assign line numbers in order of appearance
        val (idCnt, taskLine) = ids.next()
        val id = parmMap["id"]?.toString() ?: "${type.fqn}-$idCnt"
        val taskId = TaskId(group, stream, type, id)
        val condition = parmMap["condition"] as String?
        val scoped = parmMap["scoped"] as Boolean? ?: false

        val parms = parmMap
                .filterKeys { !arrayOf("id", "task", "type", "tasks", "condition", "scoped").contains(it) }
                .filterValues { it != null }
        val binding = type.module?.binding<Executable>(type, parms)
                ?: throw IllegalArgumentException("$task does not exist either module or task does not exist")

        val defn = ExecutableDefinition(type = type,
                id = taskId,
                condition = condition?.let { scriptCondition(it) } ?: defaultCondition(),
                binding = binding,
                lineNumber = taskLine,
                source = source,
                scoped = scoped
        )
        if (parmMap.containsKey("tasks")) {
            @Suppress("UNCHECKED_CAST")
            val subTasks = parmMap["tasks"] as List<Map<String, Any?>>
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