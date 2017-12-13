package io.codestream.modules.atlassian.jira

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.Server
import io.codestream.util.rest.Request
import javax.validation.constraints.NotBlank

class AssignIssueTask : BaseJiraTask() {


    @TaskProperty
    @NotBlank
    var issue:String = ""

    @TaskProperty
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