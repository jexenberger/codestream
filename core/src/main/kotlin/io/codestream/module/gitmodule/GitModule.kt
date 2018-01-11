package io.codestream.module.gitmodule

import io.codestream.core.Executable
import io.codestream.core.Factory
import io.codestream.core.Module
import io.codestream.core.TaskType
import io.codestream.runtime.classimpl.taskFromClass

class GitModule(override val name: String = "git",
                override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>> = mutableMapOf()) : Module {
    override val description: String
        get() = "Module for working with GIT Source Control"

    init {
        define {
            task(taskFromClass(CloneTask::class))
            task(taskFromClass(CheckoutTask::class))
            task(taskFromClass(FetchTask::class))
            task(taskFromClass(CommitTask::class))
            task(taskFromClass(PushTask::class))
            task(taskFromClass(BranchTask::class))
            task(taskFromClass(CommitIDTask::class))
            task(taskFromClass(PullTask::class))
        }
    }

    override fun functionObject(): Any? {
        return GitFunctions()
    }
}