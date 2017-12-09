package io.codestream.runtime

import io.codestream.core.*
import io.codestream.util.Either
import io.codestream.util.bind
import io.codestream.util.fail
import io.codestream.util.ok
import java.util.*
import java.util.concurrent.Future

class RunGroupTask<T : GroupTask>(override val defn: ExecutableDefinition<T>,
                                  val runnables: Array<RunExecutable<*>>,
                                  override val id: Int = RunExecutable.nextId(),
                                  override var state: RunExecutableState = RunExecutableState.Pending) : RunExecutable<T> {

    override fun run(ctx: StreamContext): TaskError? {
        state = RunExecutableState.Running
        val (task, theError) = defn.module.createGroupTask(defn, ctx)
        if (theError != null) {
            return theError
        }
        try {
            var error: TaskError? = null
            var resultAction: GroupTask.AfterAction = GroupTask.AfterAction.Return
            do {
                @Suppress("UNCHECKED_CAST")
                val exec = runGroupTask(task!! as T, ctx)
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
            task!!.onError(defn.id, ctx, e)
            return taskFailedWithException(defn.id, "${e::class.qualifiedName.toString()} -> ${e.message ?: "<NO MESSAGE>"}")
        }
    }

    private fun runGroupTask(task: T, ctx: StreamContext): Either<GroupTask.AfterAction, TaskError> {
        return defn.binding(defn.id, ctx, task)?.let {
            fail<GroupTask.AfterAction, TaskError>(it)
        } ?:
                if (defn.condition(defn.id, ctx)) {
                    task.before(defn.id, ctx)
                            .bind { action ->
                                if (action == GroupTask.BeforeAction.Continue) {
                                    val errorList = runLeaves(ctx, task)
                                    if (errorList.isNotEmpty())
                                        fail(subTasksFailed(errorList))
                                    else {
                                        ok<GroupTask.BeforeAction, TaskError>(action)
                                    }
                                } else {
                                    ok(action)
                                }
                            }
                            .bind { action ->
                                if (action == GroupTask.BeforeAction.Continue) {
                                    task.after(defn.id, ctx)
                                } else {
                                    ok(GroupTask.AfterAction.Return)
                                }
                            }
                } else {
                    state = RunExecutableState.Skipped
                    ok(GroupTask.AfterAction.Skipped)
                }
    }

    private fun subTasksFailed(errorList: MutableList<TaskError>) =
            TaskError(defn.id, "GroupTaskFailed", "GroupTask failed", errorList.toTypedArray())

    private fun runLeaves(ctx: StreamContext, groupTask: GroupTask): MutableList<TaskError> {
        val errorList = mutableListOf<TaskError>()
        val futuresList = mutableListOf<Future<TaskError?>>()
        val grpId = UUID.randomUUID().toString()
        try {
            runnables.forEach { runTask ->
                val taskFuture = TaskQueues.run(grpId, { runTask.run(ctx) })
                if (groupTask.async) {
                    futuresList += taskFuture
                } else {
                    taskFuture.get()?.let { errorList += it }
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