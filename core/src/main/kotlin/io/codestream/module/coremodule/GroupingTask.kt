package io.codestream.module.coremodule

import io.codestream.core.GroupTask
import io.codestream.core.TaskDescriptor
import io.codestream.core.TaskError
import io.codestream.core.TaskId
import io.codestream.runtime.StreamContext
import io.codestream.util.Either
import io.codestream.util.ok

@TaskDescriptor("group", description = "Groups a set of functions together, will not evaluate if condition is false")
class GroupingTask : GroupTask {
    override fun before(id: TaskId, ctx: StreamContext): Either<GroupTask.BeforeAction, TaskError> {
        //empty nothing to do, this task is just to group a set of sub module
        return ok(GroupTask.BeforeAction.Continue)
    }

    override fun after(id: TaskId, ctx: StreamContext): Either<GroupTask.AfterAction, TaskError> {
        //empty nothing to do, this task is just to group a set of sub module
        return ok(GroupTask.AfterAction.Return)
    }

}