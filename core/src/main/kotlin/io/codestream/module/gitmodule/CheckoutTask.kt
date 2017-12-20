package io.codestream.module.gitmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.git.GitRepository
import javax.validation.constraints.NotBlank

@TaskDescriptor("checkout", description = "Create a new branch on an existing GIT repo")
class CheckoutTask : Task {

    @TaskProperty(description = "Path to GIT repo on local filesystem")
    @get:NotBlank
    var repoPath = ""

    @TaskProperty(description = "Name of branch to checkout")
    @get:NotBlank
    var branch = ""

    @TaskProperty(description = "Name of remote, default is 'origin'")
    @NotBlank
    var remote = "origin"

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val repo = GitRepository(repoPath, remote)
        repo.checkout(branch)
        return done()
    }
}