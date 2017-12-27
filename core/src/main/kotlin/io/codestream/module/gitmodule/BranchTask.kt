package io.codestream.module.gitmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.git.GitRepository
import javax.validation.constraints.NotBlank

@TaskDescriptor("branch", description = "Create a new branch on an existing GIT repo")
class BranchTask : BaseGitTask() {


    @TaskProperty(description = "Name of the new branch")
    @get:NotBlank
    var branch = ""

    @TaskProperty(description = "Force branch")
    var force = false

    override fun doWithRepo(repo: GitRepository, id: TaskId, ctx: StreamContext): TaskError? {
        repo.allBranches
                .filter { branch.equals(it.shortName) }
                .singleOrNull() ?: repo.branch(branch, force)
        return done()
    }


}