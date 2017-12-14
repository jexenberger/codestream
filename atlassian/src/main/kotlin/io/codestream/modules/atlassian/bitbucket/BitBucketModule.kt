package io.codestream.modules.atlassian.bitbucket

import io.codestream.core.*

class BitBucketModule(
        override val name: String = "bitbucket",
        override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>>) : Module {

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