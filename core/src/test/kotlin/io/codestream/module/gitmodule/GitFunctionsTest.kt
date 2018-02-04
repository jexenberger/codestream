package io.codestream.module.gitmodule

import io.codestream.util.git.BaseGitMockServer
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull

class GitFunctionsTest : BaseGitMockServer() {


    @Before
    override fun setUp() {
        super.setUp()
        settings.clone()
    }

    @Test
    fun testRepo() {
        val repo = GitFunctions().repo(settings.repoPath.absolutePath)
        assertNotNull(repo.branch)
    }
}