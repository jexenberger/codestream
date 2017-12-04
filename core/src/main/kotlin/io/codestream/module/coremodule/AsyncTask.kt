package io.codestream.module.coremodule

import io.codestream.core.ExecutableDefinition
import io.codestream.core.GroupTask
import io.codestream.core.TaskError
import io.codestream.core.done
import io.codestream.runtime.StreamContext
import io.codestream.util.Either
import io.codestream.util.ok

class AsyncTask : GroupTask {

    override val async: Boolean
        get() = true

    override fun before(defn: ExecutableDefinition, ctx: StreamContext): Either<GroupTask.BeforeAction, TaskError> {
        return ok(GroupTask.BeforeAction.Continue)
    }

    override fun after(defn: ExecutableDefinition, ctx: StreamContext): Either<GroupTask.AfterAction, TaskError> {
        return ok(GroupTask.AfterAction.Return)
    }

    override fun bind(defn: ExecutableDefinition, ctx: StreamContext): TaskError? {
        return done()
    }
}