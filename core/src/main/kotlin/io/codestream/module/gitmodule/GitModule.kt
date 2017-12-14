package io.codestream.module.gitmodule

import io.codestream.core.*

class GitModule(override val name: String = "git",
                override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>> = mutableMapOf()) : Module {

    init {
        define {
            task("clone" to taskFromClass(CloneTask::class))
            task("fetch" to taskFromClass(FetchTask::class))
            task("commit" to taskFromClass(CommitTask::class))
            task("push" to taskFromClass(PushTask::class))
            task("branch" to taskFromClass(BranchTask::class))
            task("commit-id" to taskFromClass(CommitIDTask::class))
        }
    }

    override fun functionObject(): Any? {
        return GitFunctions()
    }
}