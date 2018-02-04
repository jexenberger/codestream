package io.codestream.util.git

import io.codestream.TestSettings
import io.codestream.util.UserPassword
import io.codestream.util.git.mockserver.MockGitServer
import io.codestream.util.system
import org.junit.After
import org.junit.Before
import java.io.File

open class BaseGitMockServer {
    private val repoName = "TestGitRepository"
    protected val settings: TestSettings = TestSettings
    protected val server = GitServer(
            uri = "https://localhost:54321/git/$repoName",
            credentials = UserPassword("pass", "word"),
            disableHostNameCheck = true,
            disableSSL = true)
    protected val dir = File("${system.tempDir}/git_repos")
    protected val repoPath = File(dir, repoName)
    @Before
    open fun setUp() {
        dir.deleteRecursively()
        MockGitServer.start()
    }

    @After
    open fun tearDown() {
        MockGitServer.stop()
    }
}