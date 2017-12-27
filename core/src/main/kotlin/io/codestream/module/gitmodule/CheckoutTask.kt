package io.codestream.module.gitmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.git.GitRepository
import javax.validation.constraints.NotBlank

@TaskDescriptor("checkout", description = "Create a new branch on an existing GIT repo")
class CheckoutTask : BaseGitTask() {


    @TaskProperty(description = "Name of branch to checkout")
    @get:NotBlank
    var branch = ""

    override fun doWithRepo(repo: GitRepository, id: TaskId, ctx: StreamContext): TaskError? {
        if (repo.branch.equals(branch)) {
            return done()
        }
        repo.checkout(branch)
        return done()
    }

}