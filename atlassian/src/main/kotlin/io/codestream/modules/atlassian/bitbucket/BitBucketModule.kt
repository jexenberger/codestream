package io.codestream.modules.atlassian.bitbucket

import io.codestream.core.*

class BitBucketModule(
        override val name: String = "bitbucket",
        override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>>) : Module {
    override val description: String
        get() = "Provides tasks for interacting with a LOCAL Atlassian Bitbucket instance with 1.0 api for backward compatibility"

    init {

        define {
            task("create-pr" to taskFromClass(CreatePullRequestTask::class))
            task("decline-pr" to taskFromClass(DeclinePullRequestTask::class))
        }
    }

    override fun functionObject(): Any? {
        return BitbucketFunctions()
    }
}