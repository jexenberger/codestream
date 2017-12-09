package io.codestream.module.gitmodule

import io.codestream.core.TaskError
import io.codestream.core.TaskProperty
import io.codestream.core.done
import io.codestream.util.Credentials
import io.codestream.util.git.GitRepository
import javax.validation.constraints.NotBlank

class FetchTask : BaseGitAuthenticatedTask() {


    @TaskProperty
    @NotBlank
    var repoPath: String = ""

    @TaskProperty
    @NotBlank
    var remote = "origin"


    override fun doWithCredentials(credentials: Credentials?): TaskError? {
        val repository = GitRepository(repoPath, remote, credentials)
        repository.fetch(remote)
        return done()
    }

}