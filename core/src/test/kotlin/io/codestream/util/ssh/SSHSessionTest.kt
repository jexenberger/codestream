package io.codestream.util.ssh

import io.codestream.TestSettings
import io.codestream.util.ok
import io.codestream.util.system
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class SSHSessionTest {

    val settings: TestSettings = TestSettings

    @get:Rule
    var name = TestName()

    @Before
    fun setUp() {
        println(name.methodName)
        if (name.methodName.indexOf("scp") > 0) {
            MockSSHServer.start(true)
        } else {
            MockSSHServer.start()
        }
    }

    @After
    fun tearDown() {
        MockSSHServer.stop()
    }

    @Test
    fun testExec() {

        val session = SSHSessionBuilder(settings.sshUser, settings.sshHost, port = 2022)
                .password(settings.sshPassword)
                .hostsFile(settings.sshKnownHosts)
                .strictHostChecking(false)
                .session
        session.left?.exec("ls") { line ->
            println(line)
        } ?: fail(session.right)
        session.mapL { it.disconnect() }

    }

    @Test
    fun testShell() {

        val session = SSHSessionBuilder(settings.sshUser, settings.sshHost, port = 2022)
                .password(settings.sshPassword)
                .hostsFile(settings.sshKnownHosts)
                .keepAlive()
                .strictHostChecking(false)
                .eol("\n")
                .prompt("\$")
                .emulation("vt100")
                .session

        session.left?.run {
            shell("export TESTING=123;ls") { line ->
                println(line)
            }
            shell("export TESTING=123;ls") { line ->
                println(line)
            }
            shell("echo \$TESTING") { line ->
                assertEquals("123", line)
            }
        }

    }


    @Test
    fun testScpTo() {
        val file = File.createTempFile("ssh", "test")
        file.appendText("hello world")
        val session = SSHSessionBuilder(settings.sshUser, settings.sshHost, port = 2022)
                .password(settings.sshPassword)
                .strictHostChecking(false)
                .session
        val result = session.mapL { it.scpTo(file.absolutePath, "~") ?: "success" }
        assertTrue(result.ok(), result.right)
        session.mapL { it.disconnect() }
    }

    @Test
    fun testScpFrom() {
        val file = File.createTempFile("ssh", "test")
        file.appendText("hello world")
        val builder = SSHSessionBuilder(settings.sshUser, settings.sshHost, port = 2022)
                .strictHostChecking(false)
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
        session.mapL { it.disconnect() }
    }
}