package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.Either
import io.codestream.util.ok
import javax.validation.constraints.NotBlank

class ForEachTask : GroupTask, TaskBinder {


    @TaskProperty
    @get:NotBlank
    var items: String = ""

    @TaskProperty
    @get:NotBlank
    var currentValue: String = "\$var"

    @TaskProperty
    var iteratorVar: String = "\$iterator"


    @Suppress("UNCHECKED_CAST")
    internal fun convertToIterator(id: TaskId, value: Any?, valueName: String): Iterator<Any>? {
        return value?.let {
            return when (it) {
                is Iterable<*> -> it.iterator() as Iterator<Any>
                is String -> it.split(",").map { it.trim() }.iterator()
                else -> throw invalidParameter(id, "$valueName is not an instance of Iterable or a comma delimited string")
            }
        }
    }

    override fun before(id: TaskId, ctx: StreamContext): Either<GroupTask.BeforeAction, TaskError> {
        if (ctx.containsKey(iteratorVar)) {
            @Suppress("UNCHECKED_CAST")
            return setEvaluation(ctx[iteratorVar] as Iterator<Any>, ctx)
        }

        val value = ctx.evalTo<Any>(this.items)
        val iterator = convertToIterator(id, value, this.items)
        return iterator?.let {
            ctx[iteratorVar] = it
            setEvaluation(it, ctx)
        } ?: ok(GroupTask.BeforeAction.Return)
    }

    private fun setEvaluation(it: Iterator<Any>, ctx: StreamContext): Either<GroupTask.BeforeAction, TaskError> {
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