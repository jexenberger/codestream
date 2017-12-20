package io.codestream.modules.atlassian.jira

import com.fasterxml.jackson.module.kotlin.readValue
import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.Server
import io.codestream.util.rest.Request
import javax.validation.constraints.NotBlank

@TaskDescriptor("get", description = "Retrieves a Jira issue and sets it in a variable")
class GetIssueTask : BaseJiraTask(), SetOutput {

    @get:NotBlank
    @TaskProperty(description = "Output variable to set, default is '\$jiraIssue'")
    override var outputVar: String = "\$jiraIssue"

    @get:NotBlank
    @TaskProperty(description = "Jira issue to use")
    var issue:String = ""

    override fun runAgainstServer(id: TaskId, ctx: StreamContext, jira: Server): TaskError? {
        val get = Request(
                url = jira.url,
                path = "rest/api/2/issue/$issue",
                validateHostName = false,
                validateSSL = false)
                .basicAuth(jira.user, jira.pwd)
                .get()
        if (get.status != 200) {
            return taskFailed(id, "Error getting Jira issue $issue returned -> ${get.status}:${get.responseMessage}")
        }

        val jiraIssueMap:Map<String, Any?> = mapper.readValue(get.body)
        ctx[outputVar] = jiraIssueMap
        return done()
    }
}