package io.codestream.module.gitmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.git.GitRepository

@TaskDescriptor("commit-id", description = "Gets the current commit ID from a GIT repo")
class CommitIDTask : BaseGitTask(), SetOutput {

    @TaskProperty(description = "name of output variable to set in context, default is '\$gitCommitId'")
    override var outputVar = "\$gitCommitId"

    override fun doWithRepo(repo: GitRepository, id: TaskId, ctx: StreamContext): TaskError? {
        ctx[outputVar] = repo.commitID
        return done()
    }

}
