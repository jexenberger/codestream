package io.codestream.util.ssh

import io.codestream.util.system
import org.apache.sshd.common.keyprovider.FileKeyPairProvider
import org.apache.sshd.common.keyprovider.KeyPairProvider
import org.apache.sshd.server.CommandFactory
import org.apache.sshd.server.SshServer
import org.apache.sshd.server.auth.password.PasswordAuthenticator
import org.apache.sshd.server.config.keys.DefaultAuthorizedKeysAuthenticator
import org.apache.sshd.server.scp.ScpCommandFactory
import org.apache.sshd.server.shell.ProcessShellFactory
import java.io.File
import java.nio.file.Paths
import java.security.KeyPair


object MockSSHServer {


    private var server: SshServer? = null


    fun start(scp: Boolean = false) {
        val paths = File(system.homeDir, ".ssh").list().map { Paths.get(system.homeDir, ".ssh", it) }
        server = SshServer.setUpDefaultServer()
        server?.port = 2022
        server?.keyPairProvider = FileKeyPairProvider(paths)
        server?.publickeyAuthenticator = DefaultAuthorizedKeysAuthenticator(false)
        server?.passwordAuthenticator = PasswordAuthenticator { user, pwd, session ->
            "test".equals(user) && "test!".equals(pwd)
        }
        server?.host = "localhost"
        server?.start()
        if (!scp) {
            server?.commandFactory = CommandFactory { cmd ->
                ProcessShellFactory(cmd).create()
            }
        } else {
            server?.commandFactory = ScpCommandFactory()
        }
    }

    fun stop() {
        server?.stop(true)
    }

}

class MockKeyPairProvider : KeyPairProvider {
    override fun loadKeys(): MutableIterable<KeyPair> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}