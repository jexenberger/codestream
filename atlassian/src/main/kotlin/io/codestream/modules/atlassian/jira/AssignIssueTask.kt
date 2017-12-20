package io.codestream.modules.atlassian.jira

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.Server
import io.codestream.util.rest.Request
import javax.validation.constraints.NotBlank

@TaskDescriptor("assign", description = "Assigns a Jira issue to a user")
class AssignIssueTask : BaseJiraTask() {


    @TaskProperty(description = "Issue to assign")
    @get:NotBlank
    var issue:String = ""

    @TaskProperty(description = "User to assign to, if not specified it defaults to the user specified in configuration")
    var user:String? = null


    override fun runAgainstServer(id: TaskId, ctx: StreamContext, jira: Server): TaskError? {

        val userToUser = user?.let { it.trim() } ?: jira.user
        val assignee = mapOf("name" to userToUser)

        val put = Request(
                url = jira.url,
                path = "rest/api/2/issue/$issue/assignee",
                validateHostName = false,
                validateSSL = false)
                .basicAuth(jira.user, jira.pwd)
                .body(mapper.writeValueAsString(assignee))
                .put()
        if (put.status != 204) {
            return taskFailed(id,"Error assigning Jira issue ${issue} returned -> ${put.status}:${put.responseMessage}")
        }

        return done()

    }
}