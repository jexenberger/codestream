package io.codestream.module.gitmodule

import io.codestream.core.TaskError
import io.codestream.core.TaskProperty
import io.codestream.core.done
import io.codestream.util.git.GitServer
import io.codestream.util.system
import javax.validation.constraints.NotBlank


class CloneTask : BaseGitServerTask() {
    @TaskProperty
    @NotBlank
    var dir: String = system.pwd

    @TaskProperty
    var branch: String? = "master"

    @TaskProperty
    var cloneAllBranches = true


    @TaskProperty
    var remote = "origin"


    override fun doOnServer(server: GitServer): TaskError? {
        server.clone(dir, branch, remote, cloneAllBranches)
        return done()
    }


}
