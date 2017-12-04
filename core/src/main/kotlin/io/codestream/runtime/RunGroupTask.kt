package io.codestream.runtime

import io.codestream.core.*
import io.codestream.util.Either
import io.codestream.util.bind
import io.codestream.util.fail
import io.codestream.util.ok
import java.util.*
import java.util.concurrent.Future

class RunGroupTask(override val defn: ExecutableDefinition,
                   val runnables: Array<RunExecutable>,
                   override val id: Int = RunExecutable.nextId(),
                   override var state: RunExecutableState = RunExecutableState.Pending) : RunExecutable {

    override fun run(ctx: StreamContext): TaskError? {
        state = RunExecutableState.Running
        val (task, error) = defn.module.createGroupTask(defn, ctx)
        if (error != null) {
            return error
        }
        try {
            var error: TaskError? = null
            var resultAction: GroupTask.AfterAction = GroupTask.AfterAction.Return
            do {
                val exec = runGroupTask(task!!, ctx)
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
            task!!.onError(defn, ctx, e)
            return taskFailedWithException(defn.id, "${e::class.qualifiedName.toString()} -> ${e.message ?: "<NO MESSAGE>"}")
        }
    }

    private fun runGroupTask(task: GroupTask, ctx: StreamContext): Either<GroupTask.AfterAction, TaskError> {
        return task.bind(defn, ctx)?.let {
            fail<GroupTask.AfterAction, TaskError>(it)
        } ?:
                if (defn.condition(defn, ctx)) {
                    task.before(defn, ctx)
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
                                    task.after(defn, ctx)
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