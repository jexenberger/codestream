package io.codestream.util.git

import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import io.codestream.util.Credentials
import io.codestream.util.SSHKey
import io.codestream.util.UserPassword
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.TransportCommand
import org.eclipse.jgit.transport.*
import org.eclipse.jgit.transport.http.HttpConnection
import org.eclipse.jgit.transport.http.HttpConnectionFactory
import org.eclipse.jgit.transport.http.JDKHttpConnectionFactory
import org.eclipse.jgit.util.FS
import org.eclipse.jgit.util.HttpSupport
import java.io.File
import java.net.Proxy
import java.net.URL


data class GitServer(val uri: String, val credentials: Credentials?, val disableHostNameCheck: Boolean = false, val disableSSL: Boolean = false) {

    val branches: Set<GitBranch>
        get() {
            val remoteRepository = Git.lsRemoteRepository()
            setup(credentials, remoteRepository, disableHostNameCheck, disableSSL)
            return remoteRepository
                    .setHeads(true)
                    .setRemote(uri)
                    .call()
                    .map { GitBranch(it.name) }
                    .toSet()
        }


    fun clone(dir: String, branch: String? = null, remoteName: String = "origin", cloneAllBranches: Boolean = true): GitRepository {

        val cloneCommand = Git
                .cloneRepository()
                .setURI(uri)
        //doing this logic since not the cloneCommand parameter doesn't seem to work
        if (cloneAllBranches) {
            cloneCommand.setBranchesToClone(branches.map { it.shortName })
        }
        branch?.let { cloneCommand.setBranch(it) }
        setup(credentials, cloneCommand, disableHostNameCheck, disableSSL)
        cloneCommand
                .setDirectory(File(dir))
                .setCloneAllBranches(cloneAllBranches)
                .setCloneSubmodules(cloneAllBranches)
                .setRemote(remoteName)
                .setBare(false)
                .call()
        return GitRepository(dir, remoteName, credentials)
    }


    companion object {
        fun setup(credentials: Credentials?, command: TransportCommand<*, *>, disableHostNameCheck: Boolean, disableSSL: Boolean) {
            when (credentials) {
                is UserPassword -> {
                    command.setCredentialsProvider(UsernamePasswordCredentialsProvider(credentials.user, credentials.password))
                    command.setTransportConfigCallback {
                        when (it) {
                            is SshTransport -> it.sshSessionFactory = SSHConfig(disableHostNameCheck)
                            is HttpTransport -> HttpTransport.setConnectionFactory(InsecureHttpConnectionFactory(disableSSL))
                        }
                    }
                }
                is SSHKey -> command.setTransportConfigCallback {
                    val ssh = it as SshTransport
                    ssh.sshSessionFactory = KeyConfig(credentials.keyFile, disableHostNameCheck)
                }
            }
        }
    }


}

open internal class SSHConfig(val disableHostNameCheck: Boolean = true) : JschConfigSessionFactory() {
    override fun configure(hc: OpenSshConfig.Host?, session: Session?) {
        if (disableHostNameCheck) {
            session?.setConfig("StrictHostKeyChecking", "no")
        }
    }

}

internal class KeyConfig(val keyFile: String, disableHostNameCheck: Boolean = true) : SSHConfig(disableHostNameCheck) {

    override fun createDefaultJSch(fs: FS?): JSch {
        val defaultJSch = super.createDefaultJSch(fs)
        defaultJSch.addIdentity(keyFile)
        return defaultJSch

    }

}

internal class InsecureHttpConnectionFactory(val disableSSL: Boolean) : HttpConnectionFactory {
    override fun create(url: URL?): HttpConnection {
        val connection = JDKHttpConnectionFactory().create(url)
        if (disableSSL) {
            HttpSupport.disableSslVerify(connection)
        }
        return connection
    }


    override fun create(url: URL, proxy: Proxy?): HttpConnection {
        val connection = JDKHttpConnectionFactory().create(url, proxy!!)
        if (disableSSL) {
            HttpSupport.disableSslVerify(connection)
        }
        return connection
    }
}