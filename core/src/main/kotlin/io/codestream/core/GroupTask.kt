package io.codestream.core

import io.codestream.runtime.StreamContext
import io.codestream.util.Either

interface GroupTask : Executable {

    enum class BeforeAction {
        Continue,
        Return
    }

    enum class AfterAction {
        Skipped,
        Loop,
        Return
    }

    val async
        get() = false

    fun before(id: TaskId, ctx: StreamContext): Either<BeforeAction, TaskError>

    fun after(id: TaskId, ctx: StreamContext): Either<AfterAction, TaskError>

    fun onError(id: TaskId, ctx: StreamContext, errorThrown: Exception) {}

}