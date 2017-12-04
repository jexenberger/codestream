package io.codestream.module.gitmodule

import io.codestream.TestSettings
import io.codestream.core.defaultCondition
import io.codestream.module.coremodule.createTaskContext
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
        val (ctx, defn) = createTaskContext(GitModule(), "commit", condition = defaultCondition())
        val cloneTask = CloneTask()

        cloneTask.uri = settings.gitUrl
        cloneTask.dir = settings.gitWorkingDir
        cloneTask.user = settings.gitUser
        cloneTask.password = settings.gitPassword
        cloneTask.disableHostNameCheck = true

        val result = cloneTask.execute(defn.id, ctx)
        assertNull(result)
    }

    @Test
    fun testExecuteSSH() {
        val (ctx, defn) = createTaskContext(GitModule(), "clone", condition = defaultCondition())
        val cloneTask = CloneTask()

        cloneTask.uri = settings.gitSSHURL
        cloneTask.dir = settings.gitWorkingDir
        cloneTask.user = settings.gitUser
        cloneTask.password = settings.gitPassword
        cloneTask.keyFile = settings.gitKeyFile
        cloneTask.disableHostNameCheck = true
        val result = cloneTask.execute(defn.id, ctx)
        assertNull(result)


    }
}