package io.codestream.module.iomodule

import io.codestream.core.*

class IOModule(override val name: String = "io",
               override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<Executable>>> = mutableMapOf()) : Module {

    init {
        define {
            task("mkdirs" to taskFromClass(MkdirTask::class))
            task("read" to taskFromClass(ReadFileTask::class))
            task("write" to taskFromClass(WriteFileTask::class))
            task("delete" to taskFromClass(DeleteTask::class))
        }
    }

}