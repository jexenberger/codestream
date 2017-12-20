package io.codestream.module.sshmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.Either
import io.codestream.util.fail
import io.codestream.util.ok
import javax.validation.constraints.NotBlank

@TaskDescriptor("session", description = "Group tasks manages an SSH session, can be used in conjuction with shell task")
class SSHSessionGroupTask : BaseSSHHandler(), GroupTask {

    @TaskProperty(description = "name of variable to set default is '\$ssh'")
    @get:NotBlank
    var sessionVar: String = "\$ssh"

    override fun before(id: TaskId, ctx: StreamContext): Either<GroupTask.BeforeAction, TaskError> {
        val session = startSession()
        session?.let { ctx[sessionVar] = it }
        return session?.let {
            fail<GroupTask.BeforeAction, TaskError>(taskFailedWithException(id, it))
        } ?: ok(GroupTask.BeforeAction.Continue)

    }

    override fun after(id: TaskId, ctx: StreamContext): Either<GroupTask.AfterAction, TaskError> {
        shutdown()
        return ok(GroupTask.AfterAction.Return)
    }

    override fun onError(id: TaskId, ctx: StreamContext, errorThrown: Exception) {
        super.onError(id, ctx, errorThrown)
        try {
            shutdown()
        } catch (e: Exception) {
            ctx.error(id, e)
        }
    }
}