package io.codestream.runtime

import io.codestream.core.*
import io.codestream.util.*
import java.util.*
import java.util.concurrent.Future

class RunGroupTask<T : GroupTask>(override val defn: ExecutableDefinition<T>,
                                  val runnables: Array<RunExecutable<*>>,
                                  override val id: Int = RunExecutable.nextId(),
                                  override var state: RunExecutableState = RunExecutableState.Pending) : RunExecutable<T> {

    override fun run(ctx: StreamContext): TaskError? {
        var buffer = ""
        (0..ctx.depthCnt).forEach {
            buffer += "  "
        }
        ctx.echo("$buffer[${defn.type}] ~")
        val currentCtx = ctx
        state = RunExecutableState.Running
        val (task, theError) = defn.module.createGroupTask(defn, currentCtx)
        if (theError != null) {
            return theError
        }
        try {
            var error: TaskError? = null
            var resultAction: GroupTask.AfterAction = GroupTask.AfterAction.Return
            do {
                @Suppress("UNCHECKED_CAST")
                val exec = runGroupTask(task!! as T, currentCtx)
                exec.left?.let {
                    when (it) {
                        GroupTask.AfterAction.Skipped -> state = RunExecutableState.Skipped
                        else -> state = RunExecutableState.Complete
                    }
                    resultAction = it
                }
                exec.right?.let {
                    state = RunExecutableState.Failed
                    error = it
                }
            } while (resultAction == GroupTask.AfterAction.Loop)
            return error
        } catch (e: Exception) {
            state = RunExecutableState.ThrewException
            task!!.onError(defn.id, currentCtx, e)
            return taskFailedWithException(defn.id, "${e::class.qualifiedName.toString()} -> ${e.message ?: "<NO MESSAGE>"}")
        } finally {
            //println empty line break
            ctx.echo("${buffer}~")
        }

    }

    private fun runGroupTask(task: T, ctx: StreamContext): Either<GroupTask.AfterAction, TaskError> {
        val error = defn.binding(defn.id, ctx, task)
        if (error != null) {
            fail<GroupTask.AfterAction, TaskError>(error)
        }
        val evalCondition = defn.condition(defn.id, ctx)
        if (!evalCondition) {
            state = RunExecutableState.Skipped
            return ok(GroupTask.AfterAction.Skipped)
        }
        return task.before(defn.id, ctx)
                .bind { action ->
                    when (action) {
                        GroupTask.BeforeAction.Continue -> {
                            val errorList = runLeaves(ctx, task)
                            failWhen(action) {
                                errorList
                                        .takeIf { it.isNotEmpty() }
                                        ?.let { subTasksFailed(it) }
                            }

                        }
                        else -> ok(action)
                    }
                }
                .bind { action ->
                    if (action == GroupTask.BeforeAction.Continue) {
                        task.after(defn.id, ctx)
                    } else {
                        ok(GroupTask.AfterAction.Return)
                    }
                }

    }

    private fun subTasksFailed(errorList: MutableList<TaskError>) =
            TaskError(defn.id, "GroupTaskFailed", "GroupTask failed", errorList.toTypedArray())

    private fun runLeaves(ctx: StreamContext, groupTask: GroupTask): MutableList<TaskError> {
        val errorList = mutableListOf<TaskError>()
        val futuresList = mutableListOf<Future<TaskError?>>()
        val grpId = UUID.randomUUID().toString()
        try {
            for (runTask in runnables) {
                val taskFuture = TaskQueues.run(grpId, { runTask.run(ctx) })
                if (groupTask.async) {
                    futuresList += taskFuture
                } else {
                    val taskError = taskFuture.get()
                    taskError?.let { errorList += it }
                }
                if (errorList.isNotEmpty()) {
                    return errorList
                }
            }

        } finally {
            futuresList.forEach {
                it.get()?.let { errorList += it }
            }
        }
        return errorList
    }
}