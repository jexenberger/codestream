package io.codestream.module.gitmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.Credentials
import io.codestream.util.SSHKey
import io.codestream.util.UserPassword

abstract class BaseGitAuthenticatedTask : Task, TaskBinder {
    @TaskProperty
    var user: String? = null
    @TaskProperty
    var password: String? = null
    @TaskProperty
    var keyFile: String? = null
    @TaskProperty
    var disableHostNameCheck: Boolean = false
    @TaskProperty
    var disableSSLValidation: Boolean = false


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        if (user == null && (password == null && keyFile == null)) {
            return invalidParameter(id, "User defined but no password or keyfile defined")
        }
        val credentials: Credentials? = user?.let { user ->
            password?.let { pwd -> UserPassword(user, pwd) } ?: SSHKey(user, keyFile!!)
        }
        return doWithCredentials(credentials)
    }

    abstract fun doWithCredentials(credentials: Credentials?): TaskError?

}