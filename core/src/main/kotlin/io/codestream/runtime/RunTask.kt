package io.codestream.runtime

import io.codestream.core.*
import io.codestream.util.fail
import io.codestream.util.ok
import io.codestream.util.timeTaken

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
                    .flatMapL {
                        val (result, time) = timeTaken { defn.binding(defn.id, ctx, it)?.let { error -> fail<T, TaskError>(error) } ?: ok(it) }
                        ctx.log.debug(defn.id, "Binding took -> ${time} millis")
                        result
                    }
                    .whenR { ctx.error(defn.id, "Failed binding task -> $it defined on line -> ${defn.lineNumber}") }
                    .whenR { state = RunExecutableState.Failed }
                    .flatMapL {
                        ctx.log.debug(defn.id, "Running task")
                        val (result, time) = timeTaken {
                            it.execute(defn.id, ctx)?.let { error ->
                                fail<T, TaskError>(error)
                            } ?: ok(it)
                        }
                        ctx.log.debug(defn.id, "Execution took -> ${time} millis")
                        result

                    }
                    .whenL { ctx.log(defn.id, "Sucessfully ran task") }
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
            ctx.log.error(defn.id, "Task threw exception -> '${e.message}'", e)
            this.state = RunExecutableState.ThrewException
            return taskFailedWithException(defn.id, "${e::class.qualifiedName.toString()} -> ${e.message ?: "<NO MESSAGE>"}")
        }
    }

}