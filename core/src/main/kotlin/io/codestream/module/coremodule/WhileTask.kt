package io.codestream.module.coremodule

import io.codestream.core.GroupTask
import io.codestream.core.TaskDescriptor
import io.codestream.core.TaskError
import io.codestream.core.TaskId
import io.codestream.runtime.StreamContext
import io.codestream.util.Either
import io.codestream.util.ok

@TaskDescriptor("while", description = "While loop which will run based on passed condition")
class WhileTask : GroupTask {
    override fun before(id: TaskId, ctx: StreamContext): Either<GroupTask.BeforeAction, TaskError> {
        return ok(GroupTask.BeforeAction.Continue)
    }

    override fun after(id: TaskId, ctx: StreamContext): Either<GroupTask.AfterAction, TaskError> {
        return ok(GroupTask.AfterAction.Loop)
    }
}