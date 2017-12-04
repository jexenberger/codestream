package io.codestream.util.git

import io.codestream.TestSettings
import io.codestream.util.UserPassword
import org.junit.Test
import java.util.*
import kotlin.test.assertTrue

class GitRepositoryTest {

    private val settings: TestSettings = TestSettings.get()
    private val repository: GitRepository

    init {
        settings.clone()
        repository = GitRepository(settings.gitWorkingDir, "origin", UserPassword(settings.gitUser, settings.gitPassword))
    }


    @Test
    fun testFetch() {
        repository.fetch("origin")
    }

    @Test
    fun testCheckout() {
        val branch = UUID.randomUUID().toString()
        repository.branch(branch)
        repository.checkout(branch)
        repository.checkout(settings.gitBranch)
    }

    @Test
    fun testLocalBranches() {
        val localBranches = repository.localBranches
        assertTrue { localBranches.filter { it.shortName == settings.gitBranch }.size == 1 }
    }

    @Test
    fun testRemotes() {
        val remotes = repository.remotes
        assertTrue { remotes.isNotEmpty()}
    }
}