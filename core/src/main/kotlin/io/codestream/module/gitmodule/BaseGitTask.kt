package io.codestream.module.gitmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.git.GitRepository
import io.codestream.util.system
import java.io.File
import javax.validation.constraints.NotBlank

abstract class BaseGitTask : Task {
    @TaskProperty(description = "Path to GIT repo on local filesystem")
    @get:NotBlank
    var repoPath = File(system.pwd)
    @TaskProperty(description = "Name of remote, default is 'origin'")
    @NotBlank
    var remote = "origin"

    final override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        return try {
            val repo = GitRepository(repoPath.absolutePath, remote)
            doWithRepo(repo, id, ctx)
        } catch (e: Exception) {
            taskFailed(id, "Task failed with ${e.message}")
        }
    }

    protected abstract fun doWithRepo(repo: GitRepository, id: TaskId, ctx: StreamContext): TaskError?

}