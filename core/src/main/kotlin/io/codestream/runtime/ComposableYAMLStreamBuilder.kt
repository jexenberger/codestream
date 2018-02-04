package io.codestream.runtime

import io.codestream.core.*
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.nodes.MappingNode
import org.yaml.snakeyaml.nodes.Node
import org.yaml.snakeyaml.nodes.ScalarNode
import org.yaml.snakeyaml.nodes.SequenceNode
import java.io.File
import java.util.*

class ComposableYAMLStreamBuilder(val streamFile: File) {


    var streamName: String
    var group: String
    var desc: String
    val builder: StreamBuilder


    init {
        streamName = streamFile.name
        group = streamFile.absoluteFile.parentFile?.name ?: "_defaultGroup"
        desc = ""
        builder = StreamBuilder(streamName, group, desc, true)
    }

    fun build(): Stream {
        val document: MappingNode = Yaml().compose(streamFile.reader()) as MappingNode
        document.value.forEach {
            val tag = (it.keyNode as ScalarNode).value
            processNode(tag, it.valueNode)
        }
        builder.name = streamName
        builder.group = group
        builder.desc = desc

        return builder.stream

    }

    fun processNode(tag: String, node: Node) {
        when (node) {
            is ScalarNode -> processScalar(tag, node)
            is SequenceNode -> processSequence(tag, node)
        }
    }

    fun processScalar(tag: String, node: ScalarNode) {
        when (tag) {
            "stream" -> streamName = node.value
            "group" -> group = node.value
            "desc" -> desc = node.value
        }
    }

    fun processSequence(tag: String, node: SequenceNode) {
        when (tag) {
            "inputs" -> processInputs(node)
            "tasks" -> processTasks(node)
        }
    }

    private fun toBool(value: String?, default: Boolean = false): Boolean {
        return value?.let { it.trim().toLowerCase().equals("true") || it.trim().toLowerCase().equals("yes") } ?: default
    }

    private fun toMap(tag: String, node: MappingNode): Map<String, String?> {
        return node.value
                .associateBy {
                    toType<ScalarNode>(tag, it.keyNode, "tasks").value
                }.mapValues {
                    toType<ScalarNode>(tag, it.value.valueNode, "tasks").value
                }
    }

    private fun processTasks(node: SequenceNode) {
        node.value.forEach(::processTask)
    }

    private fun processTask(task: Node) {
        val taskNode = toType<MappingNode>("tasks", task)
        taskNode.value.forEach { taskElems ->
            val nameNode = toType<ScalarNode>("tasks", taskElems.keyNode).value
            val propertyMap = toType<MappingNode>(nameNode, taskElems.valueNode, "tasks")
            val type = TaskType.fromString(nameNode)
            val propertyData = mutableMapOf<String, Node?>()
            propertyMap.value.forEach { inputElem ->
                val propertyName = toType<ScalarNode>("tasks", inputElem.keyNode).value
                propertyData[propertyName] = inputElem.valueNode
            }
            val id = propertyData["id"]?.let {
                toType<ScalarNode>(nameNode, it, "tasks").value
            } ?: "${type.fqn}-${UUID.randomUUID()}"
            val taskId = TaskId(group, streamName, type, id)
            val condition = propertyData["condition"]?.let {
                scriptCondition(toType<ScalarNode>(nameNode, it, "tasks").value)
            } ?: defaultCondition()
            val scoped = propertyData["scoped"]?.let {
                toBool(toType<ScalarNode>(nameNode, it, "tasks").value)
            } ?: false

            val parms = propertyData
                    .filterKeys { !arrayOf("id", "task", "type", "tasks", "condition", "scoped").contains(it) }
                    .filterValues { it != null }
                    .mapValues {
                        when (it.value) {
                            is ScalarNode -> toType<ScalarNode>(nameNode, it.value!!, "tasks").value
                            is MappingNode -> toMap(nameNode, toType<MappingNode>(nameNode, it.value!!, "tasks"))
                            else -> throw ParseError("INVALID_DEFN", "Task parameters are malformed", it.value!!.startMark.line)
                        }
                    }

            val binding = type.module?.binding<Executable>(type, parms)
                    ?: throw ParseError("TASK_TYPE_NOT_DEFN", "$type is not registered", taskElems.keyNode.startMark.line)


            val defn = ExecutableDefinition(type = type,
                    id = taskId,
                    condition = condition,
                    binding = binding,
                    lineNumber = taskElems.keyNode.startMark.line.toLong(),
                    source = streamFile.absolutePath,
                    scoped = scoped
            )

            builder.task(defn) {
                propertyData["tasks"]?.let {
                    val subTasks = toType<SequenceNode>(nameNode, it, "tasks")
                    subTasks.value.forEach(::processTask)
                }
            }

        }
    }

    private fun processInputs(node: SequenceNode) {
        node.value.forEach(::processInput)

    }

    private fun processInput(it: Node) {
        val inputNode = toType<MappingNode>("inputs", it)
        inputNode.value.forEach { elems ->
            val nameNode = toType<ScalarNode>("inputs", elems.keyNode).value
            val propertyMap = toType<MappingNode>(nameNode, elems.valueNode, "inputs")
            val propertyData = mutableMapOf<String, ScalarNode?>()
            propertyMap.value.forEach { inputElem ->
                val propertyName = toType<ScalarNode>("inputs", inputElem.keyNode).value
                propertyData[propertyName] = toType<ScalarNode>(propertyName, inputElem.valueNode, "inputs")
            }
            val type: String = propertyData["type"]?.let { node ->
                if (!Parameter.typeNames.contains(node.value)) {
                    throw ParseError(
                            code = "INVALID_TYPE",
                            msg = "${node.value.trim()} is not a valid type",
                            line = node.startMark.line)
                }
                node.value
            } ?: "string"
            val prompt = propertyData["prompt"]?.value?.trim()
                    ?: throwError(nameNode, "prompt", elems.valueNode, "required tag")
            val required = propertyData["required"]?.value?.toBoolean() ?: true
            val default = propertyData["default"]?.value?.trim()
            builder.input(Parameter(
                    required = required,
                    name = nameNode,
                    desc = prompt.toString(),
                    stringType = type,
                    defaultString = default,
                    allowedValuesList = propertyData["allowedValues"]?.value?.trim()
            ))
        }
    }

    private inline fun <reified T> toType(tag: String, node: Node, parent: String? = null): T {
        try {
            return node as T
        } catch (e: ClassCastException) {
            val msg = parent?.let { " under tag '$parent' " } ?: ""
            throw ParseError("INVALID_DEFN", "'$tag' tag $msg $msg", node.startMark.line)
        }
    }

    private fun throwError(parent: String?, tag: String, node: Node, msg: String = "is not correctly defined") {
        val msg = parent?.let { " under tag '$parent' " } ?: ""
        throw ParseError("INVALID_DEFN", "'$tag' tag $msg $msg", node.startMark.line)
    }

}
