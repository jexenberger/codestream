package io.codestream.module.gitmodule

import io.codestream.TestSettings
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull

class GitFunctionsTest {

    private val settings: TestSettings = TestSettings.get()

    @Before
    fun setUp() {
        settings.clone()
    }

    @Test
    fun testRepo() {
        val repo = GitFunctions().repo(settings.gitWorkingDir)
        assertNotNull(repo.branch)
    }
}