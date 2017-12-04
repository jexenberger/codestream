package io.codestream.runtime

import io.codestream.core.*
import io.codestream.util.bind
import io.codestream.util.fail
import io.codestream.util.ok

class RunTask(override val defn: ExecutableDefinition,
              override val id: Int = RunExecutable.nextId(),
              override var state: RunExecutableState = RunExecutableState.Pending) : RunExecutable {

    override fun run(ctx: StreamContext): TaskError? {
        state = RunExecutableState.Running
        try {
            val task = defn.module.createTask(defn, ctx)
            val doIt = defn.condition(defn, ctx)
            if (!doIt) {
                state = RunExecutableState.Skipped
                return done()
            }
            state = RunExecutableState.Complete
            val result = task
                    .bind { it.bind(defn, ctx)?.let { fail<Task, TaskError>(it) } ?: ok(it) }
                    .bind { it.execute(defn.id, ctx)?.let { fail<Task, TaskError>(it) } ?: ok(it) }
            return result.right?.let {
                this.state = RunExecutableState.Failed
                it
            }
        } catch (e:Exception) {
            this.state = RunExecutableState.ThrewException
            return taskFailedWithException(defn.id,"${e::class.qualifiedName.toString()} -> ${e.message?:"<NO MESSAGE>"}")
        }
    }

}