package io.codestream.module.iomodule

import io.codestream.core.*

class IOModule(override val name: String = "io",
               override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>> = mutableMapOf()) : Module {
    override val description: String
        get() = "Module for performing typical IO operations"

    init {
        define {
            task(taskFromClass(CopyFileTask::class))
            task(taskFromClass(MkdirTask::class))
            task(taskFromClass(ReadFileTask::class))
            task(taskFromClass(WriteFileTask::class))
            task(taskFromClass(DeleteTask::class))
        }
    }

    override fun functionObject(): Any? {
        return IOFunctions()
    }
}