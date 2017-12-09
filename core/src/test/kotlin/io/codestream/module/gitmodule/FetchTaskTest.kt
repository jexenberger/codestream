package io.codestream.module.gitmodule

import io.codestream.TestSettings
import io.codestream.module.coremodule.testId
import io.codestream.runtime.StreamContext
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
        val ctx = StreamContext()
        val cloneTask = FetchTask()
        cloneTask.repoPath = settings.gitWorkingDir
        cloneTask.user = settings.gitUser
        cloneTask.password = settings.gitPassword

        val result = cloneTask.execute(testId(), ctx)
        assertNull(result)


    }
}