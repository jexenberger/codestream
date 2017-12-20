package io.codestream.module.gitmodule

import io.codestream.core.TaskDescriptor
import io.codestream.core.TaskError
import io.codestream.core.TaskProperty
import io.codestream.core.done
import io.codestream.util.Credentials
import io.codestream.util.git.GitRepository
import io.codestream.util.system
import javax.validation.constraints.NotBlank

@TaskDescriptor("push", description = "Pushes a GIT repo to an upstream repo")
class PushTask : BaseGitAuthenticatedTask() {


    @TaskProperty(description = "Path to GIT repo")
    @NotBlank
    var repoPath = system.pwd

    @TaskProperty(description = "Force push, default is 'false'")
    @get:NotBlank
    var force = false

    @TaskProperty(description = "Push tags")
    @get:NotBlank
    var pushTags = false

    @TaskProperty(description = "Upstream repo, default is 'origin'")
    @get:NotBlank
    var remote = "origin"

    @TaskProperty(description = "Branch to push, default is 'master'")
    @get:NotBlank
    var branch = "master"

    override fun doWithCredentials(credentials: Credentials?): TaskError? {
        val repository = GitRepository(repoPath, remote, credentials)
        repository.push(branch, remote, credentials, force, pushTags)
        return done()
    }

}