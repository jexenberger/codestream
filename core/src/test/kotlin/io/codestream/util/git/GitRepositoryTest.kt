package io.codestream.util.git

import io.codestream.TestSettings
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GitRepositoryTest {

    private val settings: TestSettings = TestSettings.get()
    private val repository: GitRepository

    init {
        settings.clone()
        repository = settings.getRepo("origin")
    }


    @Test
    fun testFetch() {
        repository.fetch()
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
        assertTrue { remotes.isNotEmpty() }
    }

    @Test
    fun testCommitId() {
        val id = repository.commitID
        assertNotNull(id)
        println(id)
    }

    @Test
    fun testPush() {
        val brn = UUID.randomUUID().toString()
        repository.branch(brn)
        repository.checkout(brn)
        val path = "${settings.gitWorkingDir}/$brn"
        File(path).appendText("hello")
        repository.add(brn)
                .commit("Testing $brn")
                .push(branch = brn, force = true)
    }
}