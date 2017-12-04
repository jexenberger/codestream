package io.codestream.modules.atlassian.jira

import io.codestream.core.defaultCondition
import io.codestream.module.coremodule.CoreModule
import io.codestream.module.coremodule.createTaskContext
import io.codestream.modules.atlassian.AtlassianTestSettings
import org.junit.Test
import kotlin.test.assertNull

class SetStatusTaskTest {


    val settings: AtlassianTestSettings = AtlassianTestSettings.get()

    @Test
    fun testExecute() {
        val (ctx, defn) = createTaskContext(CoreModule(), "exec", condition = defaultCondition())
        val assignTask = SetStatusTask()

        assignTask.server = settings.jiraServer
        assignTask.issue = settings.jiraTestIssue
        assignTask.status = settings.jiraTestStatus
        val execute = assignTask.execute(defn.id, ctx)
        assertNull(execute)
    }

}