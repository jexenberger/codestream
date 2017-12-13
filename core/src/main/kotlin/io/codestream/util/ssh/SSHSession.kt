package io.codestream.util.ssh

import com.jcraft.jsch.Session
import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream


class SSHSession(val session: Session,
                 val xForwarding: Boolean = false,
                 val prompt: String,
                 val eol: String,
                 val emulation: String?) : Closeable {

    override fun close() {
        disconnect()
    }

    val shell: SSHShell by lazy {
        SSHShell(session, prompt, eol, emulation)
    }

    fun exec(): SSHExec {
        return SSHExec(session, xForwarding)
    }


    fun exec(cmd: String, handler: (String) -> Unit) {
        exec().run(cmd, handler)
    }

    fun exec(cmd: String, handler: (InputStream, OutputStream, InputStream) -> Unit) {
        exec().run(cmd, handler)
    }

    fun scp(): SCP {
        return SCP(this)
    }

    fun scpTo(localFile: String, remoteFile: String): String? {
        return scp().to(localFile, remoteFile)
    }

    fun scpFrom(localPath: String, remoteFile: String): String? {
        return scp().from(localPath, remoteFile)
    }

    fun shell(cmd: String, handler: (String) -> Unit) {
        shell.exec(cmd, handler)
    }

    fun shell(): SSHShell {
        return shell
    }

    fun run(run: SSHSession.() -> Unit) {
        try {
            run()
        } finally {
            disconnect()
        }
    }


    fun disconnect() {
        session.disconnect()
    }


}