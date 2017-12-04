package io.codestream.util.git

import io.codestream.TestSettings
import io.codestream.util.SSHKey
import io.codestream.util.UserPassword
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GitServerTest {


    private val settings: TestSettings = TestSettings.get()

    @Before
    fun setUp() {
        settings.deleteExistingGitDir()
    }

    @Test
    fun testBranches() {
        val server = GitServer(settings.gitUrl, UserPassword(settings.gitUser, settings.gitPassword), true, true)
        val branches = server.branches
        //should at least be one branch
        assertTrue { branches.size > 0 }
    }

    @Test
    fun testClone() {
        val server = GitServer(settings.gitUrl, UserPassword(settings.gitUser, settings.gitPassword), true, true)
        val repository = server.clone(settings.gitWorkingDir, settings.gitBranch, "origin", true)
        assertTrue { File(settings.gitWorkingDir,".git").exists() }
        assertEquals(settings.gitWorkingDir,repository.repo)
    }

    @Test
    fun testCloneSSH() {
        val server = GitServer(settings.gitSSHURL, SSHKey(settings.sshUser, settings.gitKeyFile),disableHostNameCheck = true)
        val repository = server.clone(settings.gitWorkingDir, settings.gitBranch, "origin", true)
        assertTrue { File(settings.gitWorkingDir,".git").exists() }
        assertEquals(settings.gitWorkingDir,repository.repo)
    }
}