package io.codestream.module.iomodule

import io.codestream.core.Executable
import io.codestream.core.Factory
import io.codestream.core.Module
import io.codestream.core.TaskType
import io.codestream.runtime.classimpl.taskFromClass

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
            task(taskFromClass(AppendFileTask::class))
            task(taskFromClass(DeleteTask::class))
        }
    }

    override fun functionObject(): Any? {
        return IOFunctions()
    }
}