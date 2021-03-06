package io.codestream.module.gitmodule

import io.codestream.module.coremodule.testId
import io.codestream.runtime.StreamContext
import io.codestream.util.git.BaseGitMockServer
import io.codestream.util.git.GitRepository
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.test.assertNull

class PushTaskTest : BaseGitMockServer() {

    private lateinit var repository: GitRepository

    @Before
    override fun setUp() {
        super.setUp()
        settings.clone()
        repository = settings.getRepo()
    }

    @Test
    fun testExecute() {
        val task = PushTask()
        val ctx = StreamContext()

        val brn = UUID.randomUUID().toString()
        repository.branch(brn)
        repository.checkout(brn)
        val path = "${settings.gitWorkingDir}/$brn"
        File(path).appendText("hello")
        repository.add(brn)
                .commit("Testing $brn")

        task.user = settings.gitUser
        task.password = settings.gitPassword
        task.repoPath = settings.repoPath
        task.force = true
        task.pushTags = true
        task.remote = "origin"
        task.branch = brn

        val result = task.execute(testId(), ctx)
        assertNull(result)
    }
}