package io.codestream.modules.atlassian.bitbucket

import io.codestream.core.*
import io.codestream.runtime.StreamContext

class CreatePullRequestTask : Task, TaskBinder {
    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        return done()
    }
}