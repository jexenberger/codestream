package io.codestream.core

import io.codestream.runtime.StreamContext
import io.codestream.util.Either
import io.codestream.util.ok

class MockGroupTask : GroupTask, TaskBinder {

    var cnt: Int = 0

    override fun before(defn: ExecutableDefinition, ctx: StreamContext): Either<GroupTask.BeforeAction, TaskError> {
        if ((ctx.containsKey("throwError") && ctx["throwError"] as Boolean)) {
            throw RuntimeException("Simulated Error")
        }
        if (cnt == 9) {
            return ok(GroupTask.BeforeAction.Return)
        }
        return ok(GroupTask.BeforeAction.Continue)
    }

    override fun after(defn: ExecutableDefinition, ctx: StreamContext): Either<GroupTask.AfterAction, TaskError> {
        cnt++
        ctx["theMockCounter"] = cnt
        return ok(GroupTask.AfterAction.Loop)
    }

    override fun onError(defn: ExecutableDefinition, ctx: StreamContext, errorThrown: Exception) {
        ctx["theMockException"] = errorThrown
        return super.onError(defn, ctx, errorThrown)
    }
}