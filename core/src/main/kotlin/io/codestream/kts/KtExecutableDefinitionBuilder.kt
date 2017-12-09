package io.codestream.kts

import io.codestream.core.*

class KtExecutableDefinitionBuilder(val group: String, val stream: String, val module: String, val name: String, val id: String) {

    val defn: ExecutableDefinition<*>
        get() {
            val type = TaskType(module, name)
            val id = TaskId(group, stream, id)
            return ExecutableDefinition(type, id, MapBinding(id, type, parms).toBinding(), condition)
        }

    private var condition: Conditional = defaultCondition()
    private var parms: MutableMap<String, Any?> = mutableMapOf()


    fun parms(init: KtExecutableDefinitionBuilder.() -> Unit): KtExecutableDefinitionBuilder {
        init()
        return this
    }

    fun parm(name: String, value: Any): KtExecutableDefinitionBuilder {
        parms[name] = value
        return this
    }

    fun onlyIf(condition: Conditional): KtExecutableDefinitionBuilder {
        this.condition = condition
        return this
    }

}