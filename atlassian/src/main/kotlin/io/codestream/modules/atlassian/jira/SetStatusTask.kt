package io.codestream.modules.atlassian.jira

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.Server
import io.codestream.util.rest.Request
import javax.validation.constraints.NotBlank

class SetStatusTask : BaseJiraTask() {

    @TaskProperty
    @NotBlank
    var issue:String = ""

    @TaskProperty
    @NotBlank
    var status:String = ""

    override fun runAgainstServer(id: TaskId, ctx: StreamContext, jira: Server): TaskError? {

        val transition = mapOf("transition" to mapOf("id" to status))

        val post = Request(
                url = jira.url,
                path = "rest/api/2/issue/$issue/transitions",
                validateHostName = false,
                validateSSL = false)
                .basicAuth(jira.user, jira.pwd)
                .body(mapper.writeValueAsString(transition))
                .post()
        if (post.status != 204) {
            return taskFailed(id,"Error set Jira issue state for ${issue} returned -> ${post.status}:${post.responseMessage}")
        }

        return done()
    }
}