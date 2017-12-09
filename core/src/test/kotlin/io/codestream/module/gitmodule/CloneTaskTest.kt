package io.codestream.module.gitmodule

import io.codestream.TestSettings
import io.codestream.module.coremodule.testId
import io.codestream.runtime.StreamContext
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNull

class CloneTaskTest {

    private val settings:TestSettings = TestSettings.get()


    @Before
    fun setUp() {
        settings.deleteExistingGitDir()
    }

    @Test
    fun testExecute() {
        val ctx = StreamContext()
        val cloneTask = CloneTask()

        cloneTask.uri = settings.gitUrl
        cloneTask.dir = settings.gitWorkingDir
        cloneTask.user = settings.gitUser
        cloneTask.password = settings.gitPassword
        cloneTask.disableHostNameCheck = true

        val result = cloneTask.execute(testId(), ctx)
        assertNull(result)
    }

    @Test
    fun testExecuteSSH() {
        val cloneTask = CloneTask()
        val ctx = StreamContext()

        cloneTask.uri = settings.gitSSHURL
        cloneTask.dir = settings.gitWorkingDir
        cloneTask.user = settings.gitUser
        cloneTask.password = settings.gitPassword
        cloneTask.keyFile = settings.gitKeyFile
        cloneTask.disableHostNameCheck = true
        val result = cloneTask.execute(testId(), ctx)
        assertNull(result)


    }
}