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

    fun before(defn: ExecutableDefinition, ctx: StreamContext): Either<BeforeAction, TaskError>

    fun after(defn: ExecutableDefinition, ctx: StreamContext): Either<AfterAction, TaskError>

    fun onError(defn: ExecutableDefinition, ctx: StreamContext, errorThrown: Exception) {}

}