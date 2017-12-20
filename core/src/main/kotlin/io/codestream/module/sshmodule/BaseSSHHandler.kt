package io.codestream.module.sshmodule

import io.codestream.core.TaskError
import io.codestream.core.TaskProperty
import io.codestream.util.ssh.SSHSession
import io.codestream.util.ssh.SSHSessionBuilder
import io.codestream.util.system
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

open class BaseSSHHandler {
    @TaskProperty(description = "Host name or ip")
    @get:NotBlank
    var host: String = ""
    @TaskProperty(description = "Host port, default is '22'")
    @get:Min(1)
    var port: Int = 22
    @TaskProperty(description = "Host user")
    var user: String = ""
    @TaskProperty(description = "Host password (not required if key file is set)")
    var password: String = ""
    @TaskProperty(description = "Hosts file, defaults to 'homedir/.ssh/known_hosts'")
    var hostsFile: String = "${system.homeDir}/.ssh/known_hosts"
    @TaskProperty(description = "Key to use for authentication if using SSH key authentication")
    var keyFile: String = ""
    @TaskProperty(description = "Password for key file")
    var keyFilePassword: String = ""

    @TaskProperty(description = "Flag to enable strict host checking, default is 'true'")
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