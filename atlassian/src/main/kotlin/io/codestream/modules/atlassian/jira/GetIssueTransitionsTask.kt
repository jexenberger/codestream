package io.codestream.modules.atlassian.jira

import com.fasterxml.jackson.module.kotlin.readValue
import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.Server
import io.codestream.util.rest.Request
import javax.validation.constraints.NotBlank

@TaskDescriptor("issue-transitions", description = "gets the issues for a transition and sets them in a Map of Transition ID to Transition Name")
class GetIssueTransitionsTask : BaseJiraTask(), SetOutput {

    @get:NotBlank
    @TaskProperty(description = "Variable to set, default is '\$transitions'")
    override var outputVar: String = "\$transitions"

    @get:NotBlank
    @TaskProperty(description = "Jira issur to get transitions from ")
    var issue:String = ""


    override fun runAgainstServer(id: TaskId, ctx: StreamContext, jira: Server): TaskError? {
        val get = Request(
                url = jira.url,
                path = "rest/api/2/issue/$issue/transitions",
                validateHostName = false,
                validateSSL = false)
                .basicAuth(jira.user, jira.pwd)
                .get()
        if (get.status != 200) {
            return taskFailed(id,"Error getting Jira issue transitions for ${issue} returned -> ${get.status}:${get.responseMessage}")
        }

        val jiraIssueMap:Map<String, Any?> = mapper.readValue(get.body)
        @Suppress("UNCHECKED_CAST")
        val transitions = jiraIssueMap["transitions"] as List<Map<String, Any?>>
        val mapping:Map<String,String> = transitions
                .associateBy { it["name"] as String }
                .mapValues { (_, value) ->
                    value["id"] as String
                }
        ctx[outputVar] = mapping
        return done()
    }
}