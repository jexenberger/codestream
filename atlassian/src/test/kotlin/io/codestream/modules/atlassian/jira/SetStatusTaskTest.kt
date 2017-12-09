package io.codestream.modules.atlassian.jira

import io.codestream.module.coremodule.testId
import io.codestream.modules.atlassian.AtlassianTestSettings
import io.codestream.runtime.StreamContext
import org.junit.Test
import kotlin.test.assertNull

class SetStatusTaskTest {


    val settings: AtlassianTestSettings = AtlassianTestSettings.get()

    @Test
    fun testExecute() {
        val ctx = StreamContext()
        val assignTask = SetStatusTask()

        assignTask.server = settings.jiraServer
        assignTask.issue = settings.jiraTestIssue
        assignTask.status = settings.jiraTestStatus
        val execute = assignTask.execute(testId(), ctx)
        assertNull(execute)
    }

}