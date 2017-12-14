package io.codestream.module.gitmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.git.GitRepository
import io.codestream.util.system
import javax.validation.constraints.NotBlank

class CommitTask : Task, TaskBinder {

    @TaskProperty
    @NotBlank
    var repoPath: String = system.pwd

    @TaskProperty
    @NotBlank
    var message: String = ""

    @TaskProperty
    var all: Boolean = true

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val repository = GitRepository(repoPath, "origin")
        repository.commit(message, all)
        return done()
    }
}