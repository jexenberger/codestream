package io.codestream.module.utilmodule

import io.codestream.core.Executable
import io.codestream.core.Factory
import io.codestream.core.Module
import io.codestream.core.TaskType
import io.codestream.runtime.classimpl.taskFromClass

class UtilModule(override val name: String = "util",
                 override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>> = mutableMapOf()) : Module {
    override val description = "A set of utility tasks"


    init {
        define {
            task(taskFromClass(SavePropertiesTask::class))
            task(taskFromClass(LoadPropertiesTask::class))
            task(taskFromClass(YamlResourcesTask::class))
            task(taskFromClass(YamlResourceDefinitionsTask::class))
        }
    }


}