package io.codestream.module.gitmodule

import io.codestream.core.TaskError
import io.codestream.core.TaskId
import io.codestream.core.TaskProperty
import io.codestream.util.Credentials
import io.codestream.util.git.GitServer
import javax.validation.constraints.NotBlank

abstract class BaseGitServerTask : BaseGitAuthenticatedTask() {
    @TaskProperty(description = "URI of the GIT server")
    @NotBlank
    var uri: String = ""

    override fun doWithCredentials(id: TaskId, credentials: Credentials?): TaskError? {
        val server = GitServer(uri, credentials, disableHostNameCheck, disableSSLValidation)
        return doOnServer(id, server)
    }

    abstract fun doOnServer(id: TaskId, server: GitServer): TaskError?

}