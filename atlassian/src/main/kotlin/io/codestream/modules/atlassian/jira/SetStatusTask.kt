package io.codestream.modules.atlassian.jira

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.Server
import io.codestream.util.rest.Request
import javax.validation.constraints.NotBlank

@TaskDescriptor("set-status", description = "Sets the status for a Jira issue")
class SetStatusTask : BaseJiraTask() {

    @TaskProperty(description = "Jira issue to set the status of")
    @get:NotBlank
    var issue:String = ""

    @TaskProperty(description = "Status to set the Jira issue to (This is the status ID)")
    @get:NotBlank
    var status:String = ""

    override fun runAgainstServer(id: TaskId, ctx: StreamContext, jira: Server): TaskError? {

        val transition = mapOf("transition" to mapOf("id" to status))

        val post = Request(
                url = jira.url,
                path = "rest/api/2/issue/$issue/transitions",
                validateHostName = false,
                validateSSL = false,
                contentType = "application/json")
                .basicAuth(jira.user, jira.pwd)
                .body(mapper.writeValueAsString(transition))
                .post()
        if (post.status != 204) {
            return taskFailed(id,"Error set Jira issue state for ${issue} returned -> ${post.status}:${post.responseMessage}")
        }

        return done()
    }
}