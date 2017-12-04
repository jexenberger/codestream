package io.codestream.module.gitmodule

import io.codestream.TestSettings
import io.codestream.core.defaultCondition
import io.codestream.module.coremodule.createTaskContext
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNull

class CheckoutTaskTest {

    private val settings:TestSettings = TestSettings.get()

    @Before
    fun setUp() {
        settings.clone()
    }

    @Test
    fun testExecute() {
        val (ctx, defn) = createTaskContext(GitModule(), "commit", condition = defaultCondition())
        val cloneTask = CheckoutTask()
        cloneTask.repoPath = settings.gitWorkingDir
        cloneTask.branch = settings.gitBranch
        val result = cloneTask.execute(defn.id, ctx)
        assertNull(result)
    }
}