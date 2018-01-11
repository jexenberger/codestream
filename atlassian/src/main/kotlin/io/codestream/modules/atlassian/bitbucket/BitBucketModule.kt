package io.codestream.modules.atlassian.bitbucket

import io.codestream.core.Executable
import io.codestream.core.Factory
import io.codestream.core.Module
import io.codestream.core.TaskType
import io.codestream.runtime.classimpl.taskFromClass

class BitBucketModule(
        override val name: String = "bitbucket",
        override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>> = mutableMapOf()) : Module {
    override val description: String
        get() = "Provides tasks for interacting with a LOCAL Atlassian Bitbucket instance with 1.0 api for backward compatibility"

    init {

        define {
            task(taskFromClass(CreatePullRequestTask::class))
            task(taskFromClass(DeclinePullRequestTask::class))
        }
    }

    override fun functionObject(): Any? {
        return BitbucketFunctions()
    }
}