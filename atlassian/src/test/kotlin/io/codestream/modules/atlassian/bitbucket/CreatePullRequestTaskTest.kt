package io.codestream.modules.atlassian.bitbucket

import io.codestream.module.coremodule.testId
import io.codestream.modules.atlassian.AtlassianTestSettings
import io.codestream.runtime.StreamContext
import org.junit.Test
import kotlin.test.assertNotNull

class CreatePullRequestTaskTest {

    @Test
    fun testExecute() {
        val task = CreatePullRequestTask()
        val settings = AtlassianTestSettings.get()
        task.server = settings.bitbucketServer
        task.repo = settings.bitbucketRepo
        task.project = settings.bitbucketProject
        task.title = "Title"
        task.description = "Pull request of test"
        task.src = settings.bitbucketSourceBranch
        task.target = settings.bitbucketTargetBranch
        task.reviewers = arrayOf(settings.bitbucketReviewer)
        val ctx = StreamContext()
        val execute = task.execute(testId(), ctx)
        val localhost = ctx[task.outputVar]
        println(localhost)
        assertNotNull(localhost, execute?.toString())

        //Allow Bitbucket to catch up
        Thread.sleep(3000)
        val decline = DeclinePullRequestTask()
        decline.server = settings.bitbucketServer
        decline.repo = settings.bitbucketRepo
        decline.project = settings.bitbucketProject
        decline.id = localhost.toString()
        decline.execute(testId(), ctx)
    }
}