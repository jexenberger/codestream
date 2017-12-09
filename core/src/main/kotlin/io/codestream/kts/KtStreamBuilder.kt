package io.codestream.kts

import io.codestream.core.Conditional
import io.codestream.core.defaultCondition
import java.io.File
import java.util.*


fun stream(group: String = File(".").name, name: String, init: KtStreamBuilder.() -> Unit): KtStreamBuilder {
    return KtStreamBuilder(group, name).build(init)
}

class KtStreamBuilder(val group: String, val name: String) {

    fun build(init: KtStreamBuilder.() -> Unit): KtStreamBuilder {
        init()
        return this
    }

    fun task(module: String, task: String, condition: Conditional = defaultCondition(), id: String = UUID.randomUUID().toString()): KtExecutableDefinitionBuilder {
        return KtExecutableDefinitionBuilder(group, name, module, task, id)
    }

    fun groupTask(module: String, task: String, condition: Conditional = defaultCondition(), id: String = UUID.randomUUID().toString()): KtExecutableDefinitionBuilder {
        return KtExecutableDefinitionBuilder(group, name, module, task, id)
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
        return this
    }

}