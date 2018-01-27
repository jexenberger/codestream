package io.codestream.modules.atlassian.jira

import io.codestream.module.coremodule.testId
import io.codestream.modules.atlassian.BaseMockHttpServer
import io.codestream.runtime.StreamContext
import org.junit.Test
import kotlin.test.assertNull

class SetStatusTaskTest : BaseMockHttpServer() {

    @Test
    fun testExecute() {

        server?.expect()?.post()?.withPath("/rest/api/2/issue/COS-123/transitions")?.andReturn(204, "done")?.once()

        val ctx = StreamContext()
        val assignTask = SetStatusTask()

        assignTask.server = settings.jiraServer
        assignTask.issue = settings.jiraTestIssue
        assignTask.status = settings.jiraTestStatus
        val execute = assignTask.execute(testId(), ctx)
        assertNull(execute)
    }

}