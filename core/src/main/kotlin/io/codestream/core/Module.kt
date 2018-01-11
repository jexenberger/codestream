package io.codestream.core

import io.codestream.doc.ExecutableDocumentation
import io.codestream.runtime.StreamContext
import io.codestream.runtime.classimpl.DefaultGroupTaskFactory
import io.codestream.runtime.classimpl.DefaultTaskClassFactory
import io.codestream.util.Either
import io.codestream.util.fail
import io.codestream.util.ok


interface Module {

    enum class AllowedTypes {
        task,
        group
    }

    val name: String
    val factories: MutableMap<TaskType, Pair<AllowedTypes, Factory<out Executable>>>
    val description: String
    val tasks: Set<TaskType>
        get() = factories.keys

    fun documentation(type: TaskType): ExecutableDocumentation? {
        return factories[type]?.second?.documentation
    }

    fun <T : Executable> binding(type: TaskType, parms: Map<String, Any?>): Binding<T>? {
        @Suppress("UNCHECKED_CAST")
        return factories[type]?.second?.getBinding(parms) as Binding<T>
    }

    fun <T : Executable> create(type: ExecutableDefinition<T>, ctx: StreamContext): Either<out Executable, TaskError> {
        val factory = factories[type.type] ?: return fail {
            taskCreationFailed(type.id, "${type.type.fqn} is not recognized in stream ${this.name}")
        }

        @Suppress("UNCHECKED_CAST")
        return factory.second.create(type as ExecutableDefinition<Executable>, ctx, this)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Task> createTask(type: ExecutableDefinition<T>, ctx: StreamContext): Either<T, TaskError> {
        return create(type, ctx).flatMap(
                { if (it is Task) ok(it as T) else fail<T, TaskError>(isGroupTask(type.id, type.type)) },
                { fail(it) })

    }

    fun <T : GroupTask> createGroupTask(type: ExecutableDefinition<T>, ctx: StreamContext): Either<GroupTask, TaskError> {
        return create(type, ctx).apply(
                { if (it is GroupTask) ok<GroupTask, TaskError>(it) else fail(isGroupTask(type.id, type.type)) },
                { fail(it) })
    }

    fun executableType(type: TaskType): AllowedTypes? {
        return factories[type]?.first
    }

    fun typeOf(name: String): TaskType {
        return TaskType(this.name, name)
    }

    fun define(init: Module.() -> Unit) {
        @Suppress("UNUSED_EXPRESSION")
        init()
    }

    infix fun task(task: DefaultTaskClassFactory) {
        task(task.description.name to task)
    }

    infix fun groupTask(task: DefaultGroupTaskFactory) {
        groupTask(task.description.name to task)
    }

    infix fun task(task: Pair<String, Factory<Task>>) {
        val type = typeOf(task.first)
        factories[type] = AllowedTypes.task to task.second
    }

    infix fun groupTask(task: Pair<String, Factory<GroupTask>>) {
        val type = typeOf(task.first)
        factories[type] = AllowedTypes.group to task.second
    }


    fun functionObject(): Any? {
        return null
    }

    companion object {

        var moduleRegistry: MutableMap<String, Module> = mutableMapOf()

        fun clear() {
            moduleRegistry.clear()
        }

        operator fun get(module: String): Module? {
            return moduleRegistry[module]
        }

        fun forEach(handler: (Module) -> Unit) {
            moduleRegistry.forEach { k, v ->
                handler(v)
            }
        }

        operator fun plusAssign(module: Module) {
            moduleRegistry[module.name] = module
        }

        fun registerFunctionObjects(ctx: StreamContext) {
            moduleRegistry.forEach { t, u ->
                u.functionObject()?.let { ctx[t] = it }
            }
        }
    }


}