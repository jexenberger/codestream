package io.codestream.module.sshmodule

import io.codestream.core.TaskError
import io.codestream.core.TaskProperty
import io.codestream.util.ssh.SSHSession
import io.codestream.util.ssh.SSHSessionBuilder
import io.codestream.util.system

open class BaseSSHHandler {
    @TaskProperty
    var host: String = ""
    @TaskProperty
    var port: Int = 22
    @TaskProperty
    var user: String = ""
    @TaskProperty
    var password: String = ""
    @TaskProperty
    var hostsFile: String = "${system.homeDir}/.ssh/known_hosts"
    @TaskProperty
    var keyFile: String = ""
    @TaskProperty
    var keyFilePassword: String = ""

    @TaskProperty
    var strictHostChecking: Boolean = true

    private var session: SSHSession? = null

    protected fun startSession(): String? {
        val builder = SSHSessionBuilder(user, host, port)
                .strictHostChecking(strictHostChecking)
                .hostsFile(hostsFile)
        if (keyFile.isNotBlank()) {
            builder.keyFile(keyFile, keyFilePassword)
        } else {
            builder.password(password)
        }
        return builder.session.whenL { session = it }.right
    }

    protected fun shutdown() {
        session?.let { it.disconnect() }
    }


    protected fun doInSession(handler: (SSHSession) -> TaskError?): TaskError? {
        return session?.use(handler)
    }

}