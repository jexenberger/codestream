package io.codestream.module.gitmodule

import io.codestream.core.*
import io.codestream.util.Credentials
import io.codestream.util.git.GitRepository
import io.codestream.util.system
import javax.validation.constraints.NotBlank

@TaskDescriptor("pull", description = "Pulls from a GIT repo against a specified branch")
class PullTask : BaseGitAuthenticatedTask() {


    @TaskProperty(description = "Path to GIT repo")
    @get:NotBlank
    var repoPath: String = system.pwd

    @TaskProperty(description = "remote repo pull from, default is 'origin'")
    @get:NotBlank
    var remote = "origin"

    @TaskProperty(description = "branch to pull from, will default to current branch if not specified")
    var branch: String? = null


    override fun doWithCredentials(id: TaskId, credentials: Credentials?): TaskError? {
        val repo = GitRepository(repoPath, remote, credentials)
        val branchToUse = branch?.let { it } ?: repo.branch
        return if (repo.pull(branchToUse, remote)) done() else taskFailed(id, "Unable to fetch from repo on branch")
    }

}