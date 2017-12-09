package io.codestream.module.gitmodule

import io.codestream.core.TaskError
import io.codestream.core.TaskProperty
import io.codestream.util.Credentials
import io.codestream.util.git.GitServer
import javax.validation.constraints.NotBlank

abstract class BaseGitServerTask : BaseGitAuthenticatedTask() {
    @TaskProperty
    @NotBlank
    var uri: String = ""

    override fun doWithCredentials(credentials: Credentials?): TaskError? {
        val server = GitServer(uri, credentials, disableHostNameCheck, disableSSLValidation)
        return doOnServer(server)
    }

    abstract fun doOnServer(server:GitServer) : TaskError?

}