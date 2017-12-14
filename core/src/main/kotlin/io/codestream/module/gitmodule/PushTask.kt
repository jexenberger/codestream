package io.codestream.module.gitmodule

import io.codestream.core.TaskError
import io.codestream.core.TaskProperty
import io.codestream.core.done
import io.codestream.util.Credentials
import io.codestream.util.git.GitRepository
import io.codestream.util.system
import javax.validation.constraints.NotBlank

class PushTask : BaseGitAuthenticatedTask() {


    @TaskProperty
    @NotBlank
    var repoPath = system.pwd

    @TaskProperty
    @NotBlank
    var force = false

    @TaskProperty
    @NotBlank
    var pushTags = false

    @TaskProperty
    @NotBlank
    var remote = "origin"

    @TaskProperty
    @NotBlank
    var branch = "master"

    override fun doWithCredentials(credentials: Credentials?): TaskError? {
        val repository = GitRepository(repoPath, remote, credentials)
        repository.push(branch, remote, credentials, force, pushTags)
        return done()
    }

}