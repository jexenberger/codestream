package io.codestream.module.gitmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.git.GitRepository
import io.codestream.util.system
import javax.validation.constraints.NotBlank

@TaskDescriptor("branch", description = "Create a new branch on an existing GIT repo")
class BranchTask : Task {

    @TaskProperty(description = "Path to the GIT repo on the filesystem, default is 'pwd'")
    @get:NotBlank
    var repoPath = system.pwd

    @TaskProperty(description = "Name of the new branch")
    @get:NotBlank
    var branch = ""

    @TaskProperty(description = "name of remote, default is 'origin'")
    @NotBlank
    var remote = "origin"

    @TaskProperty(description = "Force branch")
    var force = false

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val repo = GitRepository(repoPath, remote)
        repo.branch(branch, force)
        return done()
    }
}