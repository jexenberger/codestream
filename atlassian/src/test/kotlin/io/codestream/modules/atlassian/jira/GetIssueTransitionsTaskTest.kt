package io.codestream.modules.atlassian.jira

import io.codestream.module.coremodule.testId
import io.codestream.modules.atlassian.AtlassianTestSettings
import io.codestream.runtime.StreamContext
import org.junit.Test
import kotlin.test.assertNotNull

class GetIssueTransitionsTaskTest {

    val settings: AtlassianTestSettings = AtlassianTestSettings.get()

    @Test
    fun testExecute() {
        val ctx = StreamContext()
        val issueTask = GetIssueTransitionsTask()
        issueTask.server = settings.jiraServer
        issueTask.issue = settings.jiraTestIssue
        issueTask.execute(testId(), ctx)
        assertNotNull(ctx[issueTask.transitionsVar])
        @Suppress("UNCHECKED_CAST")
        ctx[issueTask.transitionsVar] as Map<String, Any?>
        val result = ctx.evalScript<Any?>(issueTask.transitionsVar)
        println(result)
    }


}