package io.codestream.modules.atlassian.bitbucket

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.MapBuilder
import io.codestream.util.Server
import io.codestream.util.nested
import io.codestream.util.rest.Request
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

class CreatePullRequestTask : BaseBitbucketTask(), SetOutput {


    @TaskProperty
    @NotBlank
    override var outputVar: String = "\$bitbucketRequestId"


    @TaskProperty
    @NotBlank
    var src: String = ""

    @TaskProperty
    @NotBlank
    var project: String = ""

    @TaskProperty
    @NotBlank
    var target: String = ""

    @TaskProperty
    @NotBlank
    var description: String = ""

    @TaskProperty
    @NotBlank
    var title: String = ""

    @TaskProperty
    @NotBlank
    var repo: String = ""

    @TaskProperty
    @NotEmpty
    var reviewers: Array<String> = emptyArray()

    override fun runAgainstServer(id: TaskId, ctx: StreamContext, server: Server): TaskError? {
        val path = "$basePath/projects/$project/repos/$repo/pull-requests"
        val postBody = mapper.writeValueAsString(buildRequest())
        val response = Request(
                url = server.url,
                path = path,
                validateSSL = false,
                validateHostName = false)
                .basicAuth(server.user, server.pwd)
                .body(postBody)
                .post()
        return when (response.status) {
            201 -> {
                ctx[outputVar] = response.location?.split("/")?.lastOrNull()
                done()
            }
            else -> taskFailed(id, "Task failed returned -> ${response.status}", arrayOf(taskFailed(id, "${response.body}")))
        }
    }


    fun buildRequest(): Map<String, Any?> {

        val users = reviewers.map { mapOf("user" to mapOf("name" to it)) }

        val request = MapBuilder<String, Any?>()
        request["title"] = title
        request["description"] = description
        request["state"] = "OPEN"
        request["open"] = true
        request["closed"] = false
        request.nested("fromRef") {
            put("id" to "refs/heads/$src")
            nested("repository") {
                put("slug" to repo)
                put("name" to null)
                nested("project") {
                    put("key" to project)
                }
            }
        }
        request.nested("toRef") {
            put("id" to "refs/heads/$target")
            nested("repository") {
                put("slug" to repo)
                put("name" to null)
                nested("project") {
                    put("key" to project)
                }
            }
        }
        request["locked"] = false
        request["reviewers"] = users
        return request.toMap()
    }
}