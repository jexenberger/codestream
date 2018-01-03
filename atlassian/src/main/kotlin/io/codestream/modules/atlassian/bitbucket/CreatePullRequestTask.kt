package io.codestream.modules.atlassian.bitbucket

import com.fasterxml.jackson.module.kotlin.readValue
import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.MapBuilder
import io.codestream.util.Server
import io.codestream.util.nested
import io.codestream.util.rest.Request
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

@TaskDescriptor("create-pr", description = "Create a new Bitbucket Pull Request")
class CreatePullRequestTask : BaseBitbucketTask(), SetOutput {


    @TaskProperty(description = "Name of variable to set from PR reference, default is '\$bitbucketRequestId' ")
    @get:NotBlank
    override var outputVar: String = "\$bitbucketRequestId"


    @TaskProperty(description = "Source branch to use in PR")
    @get:NotBlank
    var src: String = ""

    @TaskProperty(description = "Bitbucket project to use")
    @get:NotBlank
    var project: String = ""

    @TaskProperty(description = "Target branch to use in PR")
    @get:NotBlank
    var target: String = ""

    @TaskProperty(description = "Description to use in the PR")
    @get:NotBlank
    var description: String = ""

    @TaskProperty(description = "Title to use in the PR")
    @get:NotBlank
    var title: String = ""

    @TaskProperty(description = "Repo slug for the PR")
    @get:NotBlank
    var repo: String = ""

    @TaskProperty(description = "User name of the reviewers who will review the PR")
    @get:NotEmpty
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
            409 -> {
                val data = mapper.readValue<Map<String, Any?>>(response.body)
                val prId: Any? = data["errors"]?.let {
                    (it as List<Map<String, Any?>>)[0]["existingPullRequest"]?.let { (it as Map<String, Any?>)["id"] }
                }
                prId?.let {
                    ctx[outputVar] = it.toString()
                    done()
                } ?: taskFailed(id, "Task failed returned -> ${response.status}", arrayOf(taskFailed(id, "${response.body}")))
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