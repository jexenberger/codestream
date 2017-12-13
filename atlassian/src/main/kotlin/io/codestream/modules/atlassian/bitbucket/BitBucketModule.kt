package io.codestream.modules.atlassian.bitbucket

import io.codestream.core.*

class BitBucketModule(
        override val name: String = "bitbucket",
        override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>>) : Module {

    init {

        define {
            task("create" to taskFromClass(CreatePullRequestTask::class))
            task("decline" to taskFromClass(DeclinePullRequestTask::class))
        }
    }

}