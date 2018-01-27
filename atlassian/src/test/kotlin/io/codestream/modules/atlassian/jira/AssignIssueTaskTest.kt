package io.codestream.modules.atlassian.jira

import io.codestream.module.coremodule.testId
import io.codestream.modules.atlassian.BaseMockHttpServer
import io.codestream.runtime.StreamContext
import org.junit.Test
import kotlin.test.assertNull


class AssignIssueTaskTest : BaseMockHttpServer() {

    @Test
    fun testExecute() {

        val json = """
            { "test":"test"}
            """.trim()

        server?.expect()?.put()?.withPath("/rest/api/2/issue/COS-123/assignee")?.andReturn(204, json)?.once()

        val jiraServer = settings.jiraServer
        val ctx = StreamContext()
        val assignTask = AssignIssueTask()
        assignTask.server = jiraServer
        assignTask.issue = settings.jiraTestIssue
        assignTask.user = settings.jiraAssignmentUser
        val execute = assignTask.execute(testId(), ctx)
        assertNull(execute)
    }
}