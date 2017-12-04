package io.codestream.module.gitmodule

import io.codestream.core.*

class GitModule(override val name: String = "git",
                override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<Executable>>> = mutableMapOf()) : Module {

    init {
        define {
            task("clone" to taskFromClass(CloneTask::class))
            task("fetch" to taskFromClass(FetchTask::class))
            task("commit" to taskFromClass(CommitTask::class))
        }
    }
}