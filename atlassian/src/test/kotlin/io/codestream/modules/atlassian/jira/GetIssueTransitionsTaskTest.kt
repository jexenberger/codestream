package io.codestream.modules.atlassian.jira

import io.codestream.module.coremodule.testId
import io.codestream.modules.atlassian.BaseMockHttpServer
import io.codestream.runtime.StreamContext
import org.junit.Test
import kotlin.test.assertNotNull

class GetIssueTransitionsTaskTest : BaseMockHttpServer() {


    @Test
    fun testExecute() {

        val json = """
            {
              "transitions": [
                {"name":"test1", "id":"1" },
                {"name":"test2", "id":"2" }]

            }
            """.trim()

        server?.expect()?.get()?.withPath("/rest/api/2/issue/COS-123/transitions")?.andReturn(200, json)?.once()


        val ctx = StreamContext()
        val issueTask = GetIssueTransitionsTask()
        issueTask.server = settings.jiraServer
        issueTask.issue = settings.jiraTestIssue
        issueTask.execute(testId(), ctx)
        assertNotNull(ctx[issueTask.outputVar])
        @Suppress("UNCHECKED_CAST")
        ctx[issueTask.outputVar] as Map<String, Any?>
        val result = ctx.evalScript<Any?>(issueTask.outputVar)
        println(result)
    }


}