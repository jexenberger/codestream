package io.codestream.util.ssh

import io.codestream.TestSettings
import io.codestream.util.ok
import io.codestream.util.system
import org.junit.Test
import java.io.File
import kotlin.test.assertTrue
import kotlin.test.fail

class SSHSessionTest {

    val settings:TestSettings = TestSettings.get()


    @Test
    fun testExec() {

        val session = SSHSessionBuilder(settings.sshUser, settings.sshHost)
                .password(settings.sshPassword)
                .hostsFile(settings.sshKnownHosts)
                .strictHostChecking(false)
                .session
        session.left?.exec("ls") { line->
            println(line)
        } ?: fail(session.right)

    }
    @Test
    fun testScpTo() {
        val file = File.createTempFile("ssh", "test")
        file.appendText("hello world")
        val session = SSHSessionBuilder(settings.sshUser, settings.sshHost)
                .password(settings.sshPassword)
                .session
        val result = session.mapL {  it.scpTo(file.absolutePath, "~") ?: "success" }
        assertTrue(result.ok(), result.right)
    }

    @Test
    fun testScpFrom() {
        val file = File.createTempFile("ssh", "test")
        file.appendText("hello world")
        val builder = SSHSessionBuilder(settings.sshUser, settings.sshHost)
                .password(settings.sshPassword)
        var session = builder
                .session
        var scpFile = settings.sshScpFile
        println(scpFile)
        val result = session.mapL {
            it.scpFrom(system.tempDir, scpFile) ?: "success"
        }
        val baseName = File(scpFile).name
        assertTrue(result.ok(), result.right)
        assertTrue { File("${system.tempDir}/$baseName").exists() }
    }
}