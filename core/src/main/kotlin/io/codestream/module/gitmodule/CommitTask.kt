package io.codestream.module.gitmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.git.GitRepository
import javax.validation.constraints.NotBlank


@TaskDescriptor("commit", description = "Commits a GIT repo")
class CommitTask : BaseGitTask() {

    @TaskProperty(description = "Commit message to use")
    @get:NotBlank
    var message: String = ""

    @TaskProperty(description = "Add all existing files not added to GIT control default is 'true'")
    var all: Boolean = true

    override fun doWithRepo(repo: GitRepository, id: TaskId, ctx: StreamContext): TaskError? {
        repo.commit(message, all)
        return done()
    }

}