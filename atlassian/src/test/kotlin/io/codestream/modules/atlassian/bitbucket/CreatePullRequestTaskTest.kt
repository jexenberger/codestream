package io.codestream.modules.atlassian.bitbucket

import io.codestream.module.coremodule.testId
import io.codestream.modules.atlassian.AtlassianTestSettings
import io.codestream.modules.atlassian.BaseMockHttpServer
import io.codestream.runtime.StreamContext
import io.fabric8.mockwebserver.utils.ResponseProvider
import io.fabric8.mockwebserver.utils.ResponseProviders
import okhttp3.Headers
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CreatePullRequestTaskTest : BaseMockHttpServer() {

    @Test
    fun testExecute() {

        val headers = Headers.of(mutableMapOf("Location " to "test/test/test"))
        val provider: ResponseProvider<Any> = ResponseProviders.of(201, { req -> "hello" })
        provider.headers = headers

        server?.expect()?.post()?.withPath("/bitbucket/projects/proj01/repos/repo01/pull-requests")?.andReply(provider)?.once()

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
        settings.bitbucketBasePath?.let { task.basePath = it }
        val ctx = StreamContext()
        val execute = task.execute(testId(), ctx)
        val localhost = ctx[task.outputVar]
        println(localhost)
        assertNotNull(localhost, execute?.toString())
        assertEquals("test", localhost)
    }
}