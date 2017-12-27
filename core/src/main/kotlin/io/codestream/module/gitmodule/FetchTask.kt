package io.codestream.module.gitmodule

import io.codestream.core.*
import io.codestream.util.Credentials
import io.codestream.util.git.GitRepository
import io.codestream.util.system
import javax.validation.constraints.NotBlank

@TaskDescriptor("fetch", description = "performs a fetch on an upstream repo")
class FetchTask : BaseGitAuthenticatedTask() {


    @TaskProperty(description = "Path to GIT repo")
    @get:NotBlank
    var repoPath: String = system.pwd

    @TaskProperty(description = "remote repo to checkout of, default is 'origin'")
    @get:NotBlank
    var remote = "origin"


    override fun doWithCredentials(id: TaskId, credentials: Credentials?): TaskError? {
        val repository = GitRepository(repoPath, remote, credentials)
        repository.fetch(remote)
        return done()
    }

}