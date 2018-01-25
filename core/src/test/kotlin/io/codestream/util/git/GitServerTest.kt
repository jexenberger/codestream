package io.codestream.util.git

import io.codestream.TestSettings
import io.codestream.util.SSHKey
import io.codestream.util.UserPassword
import io.codestream.util.git.mockserver.MockGitServer
import io.codestream.util.system
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GitServerTest {


    private val repoName = "TestGitRepository"
    private val settings: TestSettings = TestSettings
    private val server = GitServer(
            uri = "https://localhost:8080/git/$repoName",
            credentials = UserPassword("pass", "word"),
            disableHostNameCheck = true,
            disableSSL = true)

    private val dir = File("${system.tempDir}/git_repos")
    private val repoPath = File(dir, repoName)

    init {
        if (!dir.exists()) {
            dir.mkdirs()
        }
        repoPath.deleteOnExit()
        dir.deleteOnExit()


    }


    @Before
    fun setUp() {
        dir.deleteRecursively()
        MockGitServer.start()
    }

    @After
    fun tearDown() {
        MockGitServer.stop()
    }

    @Test
    fun testBranches() {
        val branches = server.branches
        //should at least be one branch
        assertTrue { branches.size > 0 }
    }

    @Test
    fun testClone() {
        val repository = server.clone(settings.gitWorkingDir, settings.gitBranch, "origin", true)
        assertTrue { File(settings.gitWorkingDir,".git").exists() }
        assertEquals(settings.gitWorkingDir,repository.repo)
    }

    @Test
    fun testCloneSSH() {
        val repository = server.clone(settings.gitWorkingDir, settings.gitBranch, "origin", true)
        assertTrue { File(settings.gitWorkingDir,".git").exists() }
        assertEquals(settings.gitWorkingDir,repository.repo)
    }
}