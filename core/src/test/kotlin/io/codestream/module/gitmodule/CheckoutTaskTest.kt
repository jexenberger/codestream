package io.codestream.module.gitmodule

import io.codestream.module.coremodule.testId
import io.codestream.runtime.StreamContext
import io.codestream.util.git.BaseGitMockServer
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNull

class CheckoutTaskTest : BaseGitMockServer() {

    @Before
    override fun setUp() {
        super.setUp()
        settings.clone()
    }

    @Test
    fun testExecute() {
        val ctx = StreamContext()
        val cloneTask = CheckoutTask()
        cloneTask.repoPath = settings.repoPath
        cloneTask.branch = "master"
        val result = cloneTask.execute(testId(), ctx)
        assertNull(result)
    }
}