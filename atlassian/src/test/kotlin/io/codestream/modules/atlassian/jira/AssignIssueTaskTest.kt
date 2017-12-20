package io.codestream.modules.atlassian.jira

import io.codestream.module.coremodule.testId
import io.codestream.modules.atlassian.AtlassianTestSettings
import io.codestream.runtime.StreamContext
import org.junit.Test
import kotlin.test.assertNull


class AssignIssueTaskTest {

    val settings: AtlassianTestSettings = AtlassianTestSettings.get()

    @Test
    fun testExecute() {
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