package io.codestream.module.coremodule

import io.codestream.core.*

class CoreModule(override val name: String = "core",
                 override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>> = mutableMapOf()) : Module {
    override val description: String
        get() = "The set of core tasks which provide general functionality"

    init {
        define {
            task(taskFromClass(StreamTask::class))
            task(taskFromClass(EchoTask::class))
            task(taskFromClass(SleepTask::class))
            task(taskFromClass(ExecTask::class))
            task(taskFromClass(ExecTask::class))
            task(taskFromClass(ExecTask::class))
            task(taskFromClass(SetVariableTask::class))
            task(taskFromClass(ScriptTask::class))
            task(taskFromClass(YamlResourcesTask::class))
            task(taskFromClass(YamlResourceDefinitionsTask::class))

            groupTask(groupFromClass(WhileTask::class))
            groupTask(groupFromClass(GroupingTask::class))
            groupTask(groupFromClass(AsyncTask::class))
            groupTask(groupFromClass(ForEachTask::class))
        }
    }
}