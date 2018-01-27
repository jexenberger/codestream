package io.codestream.modules.atlassian.jira

import io.codestream.module.coremodule.testId
import io.codestream.modules.atlassian.BaseMockHttpServer
import io.codestream.runtime.StreamContext
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GetIssueTaskTest : BaseMockHttpServer() {


    @Test
    @Suppress("UNCHECKED_CAST")
    fun testExecute() {

        val json = """
            {
              "fields": { "status": { "test" : "test", "name": "The Status" } }
            }
            """.trim()

        server?.expect()?.get()?.withPath("/rest/api/2/issue/COS-123")?.andReturn(200, json)?.once()

        val ctx = StreamContext()
        val issueTask = GetIssueTask()
        issueTask.server = settings.jiraServer
        issueTask.issue = settings.jiraTestIssue
        issueTask.execute(testId(), ctx)
        assertNotNull(ctx[issueTask.outputVar])
        val issue = ctx[issueTask.outputVar] as Map<String, Any?>
        val fields = issue["fields"] as Map<String, Any?>
        val status = fields["status"] as Map<String, Any?>
        println(status["name"])

    }

    @Test
    fun testExecuteNotExist() {
        server?.expect()?.get()?.withPath("/rest/api/2/issue/qwertyfredperte")?.andReturn(404, "sorry bud")?.once()

        val ctx = StreamContext()
        val issueTask = GetIssueTask()
        issueTask.server = settings.jiraServer
        issueTask.issue = "qywertyfredperte"
        val error = issueTask.execute(testId(), ctx)
        assertNotNull(error)
        println(error?.msg)
        assertNull(ctx[issueTask.outputVar])
    }
}