package io.codestream.module.gitmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.git.GitRepository
import io.codestream.util.system
import javax.validation.constraints.NotBlank


@TaskDescriptor("commit", description = "Commits a GIT repo")
class CommitTask : Task {

    @TaskProperty(description = "Path to GIT repo")
    @get:NotBlank
    var repoPath: String = system.pwd

    @TaskProperty(description = "Commit message to use")
    @get:NotBlank
    var message: String = ""

    @TaskProperty(description = "Add all existing files not added to GIT control default is 'true'")
    var all: Boolean = true

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val repository = GitRepository(repoPath, "origin")
        repository.commit(message, all)
        return done()
    }
}