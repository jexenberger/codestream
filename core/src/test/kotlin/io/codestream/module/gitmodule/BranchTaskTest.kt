package io.codestream.module.gitmodule

import io.codestream.TestSettings
import io.codestream.module.coremodule.testId
import io.codestream.runtime.StreamContext
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertNull

class BranchTaskTest {

    private val settings: TestSettings = TestSettings.get()

    @Before
    fun setUp() {
        settings.clone()
    }

    @Test
    fun testExecute() {
        val ctx = StreamContext()
        val task = BranchTask()
        task.repoPath = settings.gitWorkingDir
        task.branch = UUID.randomUUID().toString()
        val result = task.execute(testId(), ctx)
        assertNull(result)
    }
}