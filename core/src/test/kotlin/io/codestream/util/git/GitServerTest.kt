package io.codestream.util.git

import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GitServerTest : BaseGitMockServer() {


    init {
        if (!dir.exists()) {
            dir.mkdirs()
        }
        repoPath.deleteOnExit()
        dir.deleteOnExit()


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