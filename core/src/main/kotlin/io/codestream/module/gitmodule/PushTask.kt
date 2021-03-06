package io.codestream.module.gitmodule

import io.codestream.core.*
import io.codestream.util.Credentials
import io.codestream.util.git.GitRepository
import io.codestream.util.system
import java.io.File
import javax.validation.constraints.NotBlank

@TaskDescriptor("push", description = "Pushes a GIT repo to an upstream repo")
class PushTask : BaseGitAuthenticatedTask() {


    @TaskProperty(description = "Path to GIT repo")
    @NotBlank
    var repoPath: File = File(system.pwd)

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

    override fun doWithCredentials(id: TaskId, credentials: Credentials?): TaskError? {
        val repository = GitRepository(repoPath.absolutePath, remote, credentials)
        repository.push(branch, remote, credentials, force, pushTags)
        return done()
    }

}