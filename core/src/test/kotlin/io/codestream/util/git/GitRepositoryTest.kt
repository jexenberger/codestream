package io.codestream.util.git

import io.codestream.TestSettings
import io.codestream.util.git.mockserver.MockGitServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GitRepositoryTest {

    private val settings: TestSettings = TestSettings()
    private var repository: GitRepository? = null

    init {

    }

    @Before
    fun setUp() {
        settings.deleteExistingGitDir()
        MockGitServer.start()
        settings.clone()
        repository = settings.getRepo("origin")
    }

    @After
    fun tearDown() {
        MockGitServer.stop()
    }

    @Test
    fun testBranch() {
        val branch = repository?.branch
        assertNotNull(branch)
        println(branch)
    }


    @Test
    fun testFetch() {
        repository?.fetch()
    }

    @Test
    fun testPull() {
        assertTrue(repository!!.pull())
    }

    @Test
    fun testCheckout() {
        val branch = UUID.randomUUID().toString()
        val oldBranch = repository?.branch
        repository?.branch(branch)
        repository?.checkout(branch)
        repository?.checkout(oldBranch!!)
    }

    @Test
    fun testLocalBranches() {
        val localBranches = repository?.localBranches
        assertTrue { localBranches?.filter { it.shortName == "master" }?.size == 1 }
    }

    @Test
    fun testRemotes() {
        val remotes = repository?.remotes
        assertTrue { remotes?.isNotEmpty() ?: false}
    }

    @Test
    fun testCommitId() {
        val id = repository?.commitID
        assertNotNull(id)
        println(id)
    }

    @Test
    fun testPush() {
        val brn = UUID.randomUUID().toString()
        repository?.branch(brn)
        repository?.checkout(brn)
        val path = "${settings.repoPath.absolutePath}/$brn"
        File(path).appendText("hello")
        repository?.add(brn)?.commit("Testing $brn")?.push(branch = brn, force = true)
    }
}