package io.codestream.util.ssh

import io.codestream.TestSettings
import io.codestream.util.ok
import org.junit.Test
import kotlin.test.assertTrue

class SSHSessionBuilderTest {

    val settings: TestSettings = TestSettings.get()

    @Test
    fun testWithPassword() {
        val createSession = SSHSessionBuilder(settings.sshUser, settings.sshHost)
                .password(settings.sshPassword)
                .createSession()
        assertTrue(createSession.ok())
    }

    @Test
    fun testWithKeyfile() {
        val createSession = SSHSessionBuilder(settings.sshUser, settings.sshHost)
                .keyFile(settings.sshPrivateKey)
                .createSession()
        assertTrue(createSession.ok())
    }
}