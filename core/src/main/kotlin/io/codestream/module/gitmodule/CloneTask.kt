package io.codestream.module.gitmodule

import io.codestream.core.*
import io.codestream.util.git.GitServer
import io.codestream.util.system
import javax.validation.constraints.NotBlank

@TaskDescriptor("clone", description = "Clone a GIT repository from a GIT server")
class CloneTask : BaseGitServerTask() {
    @TaskProperty(description = "Directory in which to clone the repository, default is 'pwd'")
    @get:NotBlank
    var dir: String = system.pwd

    @TaskProperty(description = "GIT branch to clone, default is 'master'")
    var branch: String? = "master"

    @TaskProperty(description = "Option to clone all branches, default is true")
    var cloneAllBranches = true


    @TaskProperty(description = "name of remote to use, default is 'origin'")
    var remote: String? = "origin"


    override fun doOnServer(id: TaskId, server: GitServer): TaskError? {
        server.clone(dir, branch, remote, cloneAllBranches)
        return done()
    }


}
