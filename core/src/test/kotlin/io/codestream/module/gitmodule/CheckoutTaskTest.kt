package io.codestream.module.gitmodule

import io.codestream.TestSettings
import io.codestream.module.coremodule.testId
import io.codestream.runtime.StreamContext
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNull

class CheckoutTaskTest {

    private val settings:TestSettings = TestSettings()

    @Before
    fun setUp() {
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