package io.codestream.module.gitmodule

import io.codestream.core.TaskDescriptor
import io.codestream.core.TaskError
import io.codestream.core.TaskProperty
import io.codestream.core.done
import io.codestream.util.Credentials
import io.codestream.util.git.GitRepository
import javax.validation.constraints.NotBlank

@TaskDescriptor("fetch", description = "performs a fetch on an upstream repo")
class FetchTask : BaseGitAuthenticatedTask() {


    @TaskProperty(description = "Path to GIT repo")
    @get:NotBlank
    var repoPath: String = ""

    @TaskProperty(description = "remote repo to checkout of, default is 'origin'")
    @get:NotBlank
    var remote = "origin"


    override fun doWithCredentials(credentials: Credentials?): TaskError? {
        val repository = GitRepository(repoPath, remote, credentials)
        repository.fetch(remote)
        return done()
    }

}