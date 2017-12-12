package io.codestream.kts

import io.codestream.core.GroupTask
import io.codestream.core.Parameter
import io.codestream.core.Task
import io.codestream.runtime.Stream
import io.codestream.runtime.StreamBuilder
import java.io.File
import java.util.*


fun stream(group: String = File(".").name, name: String, init: KtStreamBuilder.() -> Unit): KtStreamBuilder {
    return KtStreamBuilder(group, name).build(init)
}

class KtStreamBuilder(val group: String, val name: String) {

    private var workingDefinitionBuilder: KtExecutableDefinitionBuilder<*>? = null
    private val defns: MutableList<KtExecutableDefinitionBuilder<*>> = mutableListOf()
    val inputs: MutableList<Parameter> = mutableListOf()

    val stream: Stream by lazy {
        val streamBuilder = StreamBuilder(name, group, "")
        inputs.forEach {
            streamBuilder.input(it)
        }
        defns.forEach { defn ->
            defineTask(defn, streamBuilder)
        }
        streamBuilder.stream
    }

    private fun defineTask(defn: KtExecutableDefinitionBuilder<*>, builder: StreamBuilder) {
        builder.task(defn.defn) {
            defn.subDefns.forEach { defineTask(defn, builder) }
        }
    }

    fun build(init: KtStreamBuilder.() -> Unit): KtStreamBuilder {
        init()
        return this
    }

    fun <T : Task> task(module: String, task: String, id: String = UUID.randomUUID().toString()): KtExecutableDefinitionBuilder<T> {
        val definitionBuilder = KtExecutableDefinitionBuilder<T>(group, name, module, task, id)
        workingDefinitionBuilder?.let { it += definitionBuilder }
        return definitionBuilder
    }

    fun <T : GroupTask> groupTask(module: String, task: String, builder: KtExecutableDefinitionBuilder<T>.() -> Unit, id: String = UUID.randomUUID().toString()): KtExecutableDefinitionBuilder<T> {
        val definitionBuilder = KtExecutableDefinitionBuilder<T>(group, name, module, task, id)
        workingDefinitionBuilder?.let { it += definitionBuilder }
        val oldBuilder = workingDefinitionBuilder
        workingDefinitionBuilder = definitionBuilder
        definitionBuilder.builder()
        workingDefinitionBuilder = oldBuilder
        return definitionBuilder
    }

    fun inputs(init: KtStreamBuilder.() -> Unit): KtStreamBuilder {
        init()
        return this
    }

    fun tasks(init: KtStreamBuilder.() -> Unit): KtStreamBuilder {
        init()
        return this
    }

    inline fun <reified K> input(name: String,
                                 description: String,
                                 required: Boolean = true,
                                 default: K? = null,
                                 optionsList: Array<K> = arrayOf()): KtStreamBuilder {
        inputs += Parameter(required = required,
                name = name,
                desc = description,
                type = K::class,
                defaultValue = default,
                values = optionsList)
        return this
    }

}