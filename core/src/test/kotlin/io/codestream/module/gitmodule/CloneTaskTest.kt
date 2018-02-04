package io.codestream.module.gitmodule

import io.codestream.module.coremodule.testId
import io.codestream.runtime.StreamContext
import io.codestream.util.git.BaseGitMockServer
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNull

class CloneTaskTest : BaseGitMockServer() {


    @Before
    override fun setUp() {
        super.setUp()
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
        cloneTask.disableSSLValidation = true

        val result = cloneTask.execute(testId(), ctx)
        assertNull(result)
    }

}