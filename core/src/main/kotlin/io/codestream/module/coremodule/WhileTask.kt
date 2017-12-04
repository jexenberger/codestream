package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.Either
import io.codestream.util.fail
import io.codestream.util.ok

class WhileTask : GroupTask, TaskBinder {
    override fun before(defn: ExecutableDefinition, ctx: StreamContext): Either<GroupTask.BeforeAction, TaskError> {
        return defn.condition?.let {
            ok<GroupTask.BeforeAction, TaskError>(GroupTask.BeforeAction.Continue)
        } ?: fail(taskFailed(defn.id, "While task much have a condition"))
    }

    override fun after(defn: ExecutableDefinition, ctx: StreamContext): Either<GroupTask.AfterAction, TaskError> {
        return ok(GroupTask.AfterAction.Loop)
    }
}