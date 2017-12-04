package io.codestream.core

import io.codestream.runtime.StreamContext
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

    fun create(type: ExecutableDefinition, ctx: StreamContext): Either<Executable, TaskError> {
        val factory = factories[type.type] ?: return fail {
            taskCreationFailed(type.id, "${type.type.fqn} is not recognized in stream ${this.name}")
        }
        return factory.second.create(type, ctx, this)
    }

    fun createTask(type: ExecutableDefinition, ctx: StreamContext): Either<Task, TaskError> {
        return create(type, ctx).apply(
                {if (it is Task) ok<Task, TaskError>(it) else fail(isGroupTask(type.id,type.type))},
                {fail(it)})
    }

    fun createGroupTask(type: ExecutableDefinition, ctx: StreamContext): Either<GroupTask, TaskError> {
        return create(type, ctx).apply(
                {if (it is GroupTask) ok<GroupTask, TaskError>(it) else fail(isGroupTask(type.id,type.type))},
                {fail(it)})
    }

    fun executableType(type: TaskType): AllowedTypes? {
        return factories[type]?.first
    }

    fun typeOf(name: String): TaskType {
        return TaskType(this.name, name)
    }

    fun define(init: Module.() -> Unit) {
        init()
    }

    infix fun task(task: Pair<String, Factory<Task>>) {
        val type = typeOf(task.first)
        factories[type] = AllowedTypes.task to task.second
    }

    infix fun groupTask(task: Pair<String, Factory<GroupTask>>) {
        val type = typeOf(task.first)
        factories[type] = AllowedTypes.group to task.second
    }



    companion object {

        var moduleRegistry: MutableMap<String, Module> = mutableMapOf()

        fun clear() {
            moduleRegistry.clear()
        }

        operator fun get(module: String): Module? {
            return moduleRegistry[module]
        }

        operator fun plusAssign(module: Module) {
            moduleRegistry[module.name] = module
        }
    }


}