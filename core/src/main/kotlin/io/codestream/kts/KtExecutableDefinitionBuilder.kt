package io.codestream.kts

import io.codestream.core.*
import io.codestream.runtime.StreamContext

class KtExecutableDefinitionBuilder<T : Executable>(val group: String, val stream: String, val module: String, val name: String, var id: String) {

    val subDefns: MutableList<KtExecutableDefinitionBuilder<*>> = mutableListOf()

    val defn: ExecutableDefinition<*>
        get() {
            val type = TaskType(module, name)
            val id = TaskId(group, stream, type, id)
            return ExecutableDefinition(type, id, binding, condition)
        }

    private var condition: Conditional = defaultCondition()
    private var binding: Binding<T> = emptyBinding()

    fun flatBind(binding: Binding<T>): KtExecutableDefinitionBuilder<T> {
        this.binding = binding
        return this
    }

    fun id(id: String): KtExecutableDefinitionBuilder<T> {
        this.id = id
        return this
    }

    operator fun plusAssign(subDefn: KtExecutableDefinitionBuilder<*>) {
        subDefns += subDefn
    }

    fun bindAll(func: T.(TaskId, StreamContext) -> Unit): KtExecutableDefinitionBuilder<T> {
        this.binding = { id, ctx, task ->
            task.func(id, ctx)
            done()
        }
        return this
    }

    fun bind(func: T.() -> Unit): KtExecutableDefinitionBuilder<T> {
        this.binding = { _, _, task ->
            task.func()
            done()
        }
        return this
    }

    fun bindWithCtx(func: T.(StreamContext) -> Unit): KtExecutableDefinitionBuilder<T> {
        this.binding = { _, ctx, task ->
            task.func(ctx)
            done()
        }
        return this
    }

    fun onlyIf(condition: Conditional): KtExecutableDefinitionBuilder<T> {
        this.condition = condition
        return this
    }

}