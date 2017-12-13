package io.codestream.modules.atlassian.bitbucket

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.Server
import io.codestream.util.rest.Request
import javax.validation.constraints.NotBlank

class DeclinePullRequestTask : BaseBitbucketTask() {


    @TaskProperty
    @NotBlank
    var project: String = ""

    @TaskProperty
    @NotBlank
    var repo: String = ""

    @TaskProperty
    @NotBlank
    var id: String = ""


    override fun runAgainstServer(id: TaskId, ctx: StreamContext, server: Server): TaskError? {
        val path = "$basePath/projects/$project/repos/$repo/pull-requests/${this.id}/decline"
        val response = Request(
                url = server.url,
                path = path,
                validateSSL = false,
                validateHostName = false)
                .basicAuth(server.user, server.pwd)
                .post()
        return when (response.status) {
            201 -> {
                done()
            }
            else -> taskFailed(id, "Task failed returned -> ${response.status}", arrayOf(taskFailed(id, response.body)))
        }
    }

}