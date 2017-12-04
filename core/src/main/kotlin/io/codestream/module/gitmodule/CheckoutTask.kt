package io.codestream.module.gitmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.git.GitRepository
import javax.validation.constraints.NotBlank

class CheckoutTask : Task, TaskBinder {

    @TaskProperty
    @NotBlank
    var repoPath = ""

    @TaskProperty
    @NotBlank
    var branch = ""

    @TaskProperty
    @NotBlank
    var remote = "origin"

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val repo = GitRepository(repoPath,remote)
        repo.checkout(branch)
        return done()
    }
}