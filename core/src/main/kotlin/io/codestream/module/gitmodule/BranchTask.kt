package io.codestream.module.gitmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.git.GitRepository
import io.codestream.util.system
import javax.validation.constraints.NotBlank

class BranchTask : Task, TaskBinder {

    @TaskProperty
    @NotBlank
    var repoPath = system.pwd

    @TaskProperty
    @NotBlank
    var branch = ""

    @TaskProperty
    @NotBlank
    var remote = "origin"

    @TaskProperty
    var force = false

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val repo = GitRepository(repoPath, remote)
        repo.branch(branch, force)
        return done()
    }
}