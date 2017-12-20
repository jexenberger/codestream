package io.codestream.runtime

import io.codestream.core.*
import io.codestream.util.fail
import io.codestream.util.ok

class RunTask<T : Task>(override val defn: ExecutableDefinition<T>,
                        override val id: Int = RunExecutable.nextId(),
                        override var state: RunExecutableState = RunExecutableState.Pending) : RunExecutable<T> {

    override fun run(ctx: StreamContext): TaskError? {
        var buffer = ""
        (0..ctx.depthCnt).forEach {
            buffer += "  "
        }
        ctx.echo("$buffer[${defn.type}]")
        state = RunExecutableState.Running
        try {
            val task = defn.module.createTask(defn, ctx)
            val doIt = defn.condition(defn.id, ctx)
            if (!doIt) {
                state = RunExecutableState.Skipped
                return done()
            }
            state = RunExecutableState.Complete
            return task.whenR { ctx.error(defn.id, "Unable to create Task -> $it") }
                    .flatMapL { defn.binding(defn.id, ctx, it)?.let { error -> fail<T, TaskError>(error) } ?: ok(it) }
                    .whenR { ctx.error(defn.id, "Failed binding task -> $it defined on line -> ${defn.lineNumber}") }
                    .whenR { state = RunExecutableState.Failed }
                    .flatMapL { it.execute(defn.id, ctx)?.let { error -> fail<T, TaskError>(error) } ?: ok(it) }
                    .whenR {
                        ctx.error(defn.id, "Failed running task -> $it")
                        ctx.error(defn.id, "SOURCE: -> '${defn.source}' @ line -> '${defn.lineNumber}'")
                    }
                    .whenR { state = RunExecutableState.Failed }
                    .map(
                            { null },
                            { it }
                    )
        } catch (e: Exception) {
            this.state = RunExecutableState.ThrewException
            return taskFailedWithException(defn.id, "${e::class.qualifiedName.toString()} -> ${e.message ?: "<NO MESSAGE>"}")
        }
    }

}