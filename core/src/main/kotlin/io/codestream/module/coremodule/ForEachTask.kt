package io.codestream.module.coremodule

import io.codestream.core.GroupTask
import io.codestream.core.TaskError
import io.codestream.core.TaskId
import io.codestream.core.TaskProperty
import io.codestream.runtime.StreamContext
import io.codestream.util.Either
import io.codestream.util.ok
import javax.validation.constraints.NotBlank

class ForEachTask : GroupTask {


    @TaskProperty
    var items: Collection<*> = emptyList<Any>()


    @TaskProperty
    @get:NotBlank
    var currentValue: String = "\$var"

    @TaskProperty
    var iteratorVar: String = "\$iterator"

    override fun before(id: TaskId, ctx: StreamContext): Either<GroupTask.BeforeAction, TaskError> {
        if (ctx.containsKey(iteratorVar)) {
            @Suppress("UNCHECKED_CAST")
            return setEvaluation(ctx[iteratorVar] as Iterator<Any>, ctx)
        }
        val iterator = items.iterator()
        ctx[iteratorVar] = iterator
        return setEvaluation(iterator, ctx)
    }

    private fun setEvaluation(it: Iterator<*>, ctx: StreamContext): Either<GroupTask.BeforeAction, TaskError> {
        return if (it.hasNext()) {
            ctx[currentValue] = it.next()
            ok(GroupTask.BeforeAction.Continue)
        } else {
            ok(GroupTask.BeforeAction.Return)
        }
    }

    override fun after(id: TaskId, ctx: StreamContext): Either<GroupTask.AfterAction, TaskError> {
        return ok(GroupTask.AfterAction.Loop)
    }
}