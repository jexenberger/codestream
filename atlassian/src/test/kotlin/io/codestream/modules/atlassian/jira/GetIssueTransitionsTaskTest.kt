package io.codestream.modules.atlassian.jira

import io.codestream.core.defaultCondition
import io.codestream.module.coremodule.CoreModule
import io.codestream.module.coremodule.createTaskContext
import io.codestream.modules.atlassian.AtlassianTestSettings
import org.junit.Test
import kotlin.test.assertNotNull

class GetIssueTransitionsTaskTest {

    val settings: AtlassianTestSettings = AtlassianTestSettings.get()

    @Test
    fun testExecute() {

        val (ctx, defn) = createTaskContext(CoreModule(), "exec", condition = defaultCondition())
        val issueTask = GetIssueTransitionsTask()
        issueTask.server = settings.jiraServer
        issueTask.issue = settings.jiraTestIssue
        issueTask.execute(defn.id, ctx)
        assertNotNull(ctx[issueTask.transitionsVar])
        ctx[issueTask.transitionsVar] as Map<String, Any?>
        val result = ctx.evalScript<Any?>(issueTask.transitionsVar)
        println(result)
    }


}