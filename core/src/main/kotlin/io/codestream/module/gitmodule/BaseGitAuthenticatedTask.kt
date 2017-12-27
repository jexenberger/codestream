package io.codestream.module.gitmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.Credentials
import io.codestream.util.SSHKey
import io.codestream.util.UserPassword

abstract class BaseGitAuthenticatedTask : Task {
    @TaskProperty(description = "GIT server user")
    var user: String? = null
    @TaskProperty(description = "GIT server password")
    var password: String? = null
    @TaskProperty(description = "SSH Keyfile path if using SSH key authentication")
    var keyFile: String? = null
    @TaskProperty(description = "disable host name checking if server is using SSL")
    var disableHostNameCheck: Boolean = false
    @TaskProperty(description = "disable ssl validation if server is using SSL")
    var disableSSLValidation: Boolean = false


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        if (user == null && (password == null && keyFile == null)) {
            return invalidParameter(id, "User defined but no password or keyfile defined")
        }
        val credentials: Credentials? = user?.let { user ->
            password?.let { pwd -> UserPassword(user, pwd) } ?: SSHKey(user, keyFile!!)
        }
        try {
            return doWithCredentials(id, credentials)
        } catch (e: Exception) {
            return taskFailed(id, "Task failed with -> ${e.message}")
        }
    }

    abstract fun doWithCredentials(id: TaskId, credentials: Credentials?): TaskError?

}