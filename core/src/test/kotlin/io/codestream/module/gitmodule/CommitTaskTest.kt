package io.codestream.module.gitmodule

import io.codestream.module.coremodule.testId
import io.codestream.runtime.StreamContext
import io.codestream.util.git.BaseGitMockServer
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNull

class CommitTaskTest : BaseGitMockServer() {

    @Before
    override fun setUp() {
        super.setUp()
        settings.clone()
    }

    @Test
    fun testExecute() {
        val ctx = StreamContext()
        val cloneTask = CommitTask()


        cloneTask.repoPath = settings.repoPath
        cloneTask.message = "a commit"

        val result = cloneTask.execute(testId(), ctx)
        assertNull(result)
    }
}