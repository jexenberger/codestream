package io.codestream.module.utilmodule

import io.codestream.core.*

class UtilModule(override val name: String = "util",
                 override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>> = mutableMapOf()) : Module {
    override val description: String
        get() = "A set of utility tasks for working such as working with .properties files"


    init {
        define {
            task(taskFromClass(SavePropertiesTask::class))
        }
    }


}