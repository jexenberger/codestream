package io.codestream.module.gitmodule

import io.codestream.module.coremodule.testId
import io.codestream.runtime.StreamContext
import io.codestream.util.git.BaseGitMockServer
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertNull

class BranchTaskTest : BaseGitMockServer() {

    @Before
    override fun setUp() {
        super.setUp()
        settings.clone()
    }


    @Test
    fun testExecute() {
        val ctx = StreamContext()
        val task = BranchTask()
        task.repoPath = settings.repoPath
        task.branch = UUID.randomUUID().toString()
        val result = task.execute(testId(), ctx)
        assertNull(result)
    }
}