package io.codestream.module.gitmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.git.GitRepository
import javax.validation.constraints.NotBlank

class CommitIDTask : Task, TaskBinder {
    @TaskProperty
    @NotBlank
    var repoPath = ""

    @TaskProperty
    @NotBlank
    var remote = "origin"

    @TaskProperty
    var outputVar = "\$gitCommitId"

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val repo = GitRepository(repoPath, remote)
        ctx[outputVar] = repo.commitID
        return done()
    }
}