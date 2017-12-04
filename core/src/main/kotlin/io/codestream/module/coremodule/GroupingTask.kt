package io.codestream.module.coremodule

import io.codestream.core.ExecutableDefinition
import io.codestream.core.GroupTask
import io.codestream.core.TaskError
import io.codestream.core.done
import io.codestream.runtime.StreamContext
import io.codestream.util.Either
import io.codestream.util.ok

class GroupingTask : GroupTask {
    override fun before(defn: ExecutableDefinition, ctx: StreamContext): Either<GroupTask.BeforeAction, TaskError> {
        //empty nothing to do, this task is just to group a set of sub module
        return ok(GroupTask.BeforeAction.Continue)
    }

    override fun after(defn: ExecutableDefinition, ctx: StreamContext): Either<GroupTask.AfterAction, TaskError> {
        //empty nothing to do, this task is just to group a set of sub module
        return ok(GroupTask.AfterAction.Return)
    }

    override fun bind(defn: ExecutableDefinition, ctx: StreamContext): TaskError? {
        //empty nothing to do, this task is just to group a set of sub module
        return done()
    }
}