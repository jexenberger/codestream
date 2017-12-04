package io.codestream.modules.atlassian.jira

import io.codestream.core.defaultCondition
import io.codestream.module.coremodule.CoreModule
import io.codestream.module.coremodule.createTaskContext
import io.codestream.modules.atlassian.AtlassianTestSettings
import org.junit.Test
import java.util.*
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GetIssueTaskTest {

    val settings: AtlassianTestSettings = AtlassianTestSettings.get()

    @Test
    fun testExecute() {

        val (ctx, defn) = createTaskContext(CoreModule(), "exec", condition = defaultCondition())
        val issueTask = GetIssueTask()
        issueTask.server = settings.jiraServer
        issueTask.issue = settings.jiraTestIssue
        issueTask.execute(defn.id, ctx)
        assertNotNull(ctx[issueTask.taskVar])
        val issue = ctx[issueTask.taskVar] as Map<String, Any?>
        val fields = issue["fields"] as Map<String, Any?>
        val status = fields["status"] as Map<String, Any?>
        println(status["name"])

    }

    @Test
    fun testExecuteNotExist() {

        val (ctx, defn) = createTaskContext(CoreModule(), "exec", condition = defaultCondition())
        val issueTask = GetIssueTask()
        issueTask.server = settings.jiraServer
        issueTask.issue = UUID.randomUUID().toString()
        val error = issueTask.execute(defn.id, ctx)
        assertNotNull(error)
        println(error?.msg)
        assertNull(ctx[issueTask.taskVar])
    }
}