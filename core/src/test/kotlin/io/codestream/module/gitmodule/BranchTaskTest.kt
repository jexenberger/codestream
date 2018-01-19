package io.codestream.module.gitmodule

import io.codestream.TestSettings
import io.codestream.module.coremodule.testId
import io.codestream.runtime.StreamContext
import io.codestream.util.git.mockserver.MockGitServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertNull

class BranchTaskTest {


    val settings = TestSettings()

    @Before
    fun setUp() {
        MockGitServer.start()
        TestSettings().clone()
    }

    @After
    fun tearDown() {
        MockGitServer.stop()
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