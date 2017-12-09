package io.codestream.module.coremodule

import io.codestream.core.*

class CoreModule(override val name: String = "core",
                 override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>> = mutableMapOf()) : Module {

    init {
        define {
            task("stream" to taskFromClass(StreamTask::class))
            task("echo" to taskFromClass(EchoTask::class))
            task("sleep" to taskFromClass(SleepTask::class))
            task("exec" to taskFromClass(ExecTask::class))
            task("dump" to taskFromClass(ExecTask::class))
            task("restore" to taskFromClass(ExecTask::class))
            task("set" to taskFromClass(SetVariableTask::class))
            task("js" to taskFromClass(ScriptTask::class))
            task("yaml-resources" to taskFromClass(YamlResourcesTask::class))
            task("yaml-definitions" to taskFromClass(YamlResourceDefinitionsTask::class))

            groupTask("while" to groupFromClass(WhileTask::class))
            groupTask("group" to groupFromClass(GroupingTask::class))
            groupTask("async" to groupFromClass(AsyncTask::class))
            groupTask("foreach" to groupFromClass(ForEachTask::class))
        }
    }
}