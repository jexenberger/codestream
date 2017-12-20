package io.codestream.module.gitmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.git.GitRepository
import javax.validation.constraints.NotBlank

@TaskDescriptor("commit-id", description = "Gets the current commit ID from a GIT repo")
class CommitIDTask : Task, SetOutput {
    @TaskProperty(description = "Path to GIT repo")
    @get:NotBlank
    var repoPath = ""

    @TaskProperty(description = "Name of remote, default is 'origin'")
    @NotBlank
    var remote = "origin"

    @TaskProperty(description = "name of output variable to set in context, default is '\$gitCommitId'")
    override var outputVar = "\$gitCommitId"

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val repo = GitRepository(repoPath, remote)
        ctx[outputVar] = repo.commitID
        return done()
    }
}