package io.codestream.module.gitmodule

import io.codestream.core.*

class GitModule(override val name: String = "git",
                override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>> = mutableMapOf()) : Module {
    override val description: String
        get() = "Module for working with GIT Source Control"

    init {
        define {
            task(taskFromClass(CloneTask::class))
            task(taskFromClass(FetchTask::class))
            task(taskFromClass(CommitTask::class))
            task(taskFromClass(PushTask::class))
            task(taskFromClass(BranchTask::class))
            task(taskFromClass(CommitIDTask::class))
        }
    }

    override fun functionObject(): Any? {
        return GitFunctions()
    }
}