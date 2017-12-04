package io.codestream.module.gitmodule

import io.codestream.TestSettings
import io.codestream.core.defaultCondition
import io.codestream.module.coremodule.createTaskContext
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNull

class FetchTaskTest {

    private val settings: TestSettings = TestSettings.get()

    @Before
    fun setUp() {
        settings.clone()
    }



    @Test
    fun testExecute() {
        val (ctx, defn) = createTaskContext(GitModule(), "fetch", condition = defaultCondition())
        val cloneTask = FetchTask()
        cloneTask.repoPath = settings.gitWorkingDir
        cloneTask.user = settings.gitUser
        cloneTask.password = settings.gitPassword

        val result = cloneTask.execute(defn.id, ctx)
        assertNull(result)


    }
}