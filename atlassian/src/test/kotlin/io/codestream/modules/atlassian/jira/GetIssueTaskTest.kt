package io.codestream.modules.atlassian.jira

import io.codestream.module.coremodule.testId
import io.codestream.modules.atlassian.AtlassianTestSettings
import io.codestream.runtime.StreamContext
import org.junit.Test
import java.util.*
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GetIssueTaskTest {

    val settings: AtlassianTestSettings = AtlassianTestSettings.get()

    @Test
    @Suppress("UNCHECKED_CAST")
    fun testExecute() {
        val ctx = StreamContext()
        val issueTask = GetIssueTask()
        issueTask.server = settings.jiraServer
        issueTask.issue = settings.jiraTestIssue
        issueTask.execute(testId(), ctx)
        assertNotNull(ctx[issueTask.taskVar])
        val issue = ctx[issueTask.taskVar] as Map<String, Any?>
        val fields = issue["fields"] as Map<String, Any?>
        val status = fields["status"] as Map<String, Any?>
        println(status["name"])

    }

    @Test
    fun testExecuteNotExist() {
        val ctx = StreamContext()
        val issueTask = GetIssueTask()
        issueTask.server = settings.jiraServer
        issueTask.issue = UUID.randomUUID().toString()
        val error = issueTask.execute(testId(), ctx)
        assertNotNull(error)
        println(error?.msg)
        assertNull(ctx[issueTask.taskVar])
    }
}