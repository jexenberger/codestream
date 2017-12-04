package io.codestream.modules.atlassian.jira

import com.fasterxml.jackson.module.kotlin.readValue
import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.rest.Request
import javax.validation.constraints.NotBlank

class GetIssueTransitionsTask : BaseJiraTask(){

    @NotBlank
    @TaskProperty
    var transitionsVar:String = "\$transitions"

    @NotBlank
    @TaskProperty
    var issue:String = ""


    override fun runAgainstJira(id: TaskId, ctx: StreamContext, jira: JiraServer): TaskError? {
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
        val transitions = jiraIssueMap["transitions"] as List<Map<String, Any?>>
        val mapping:Map<String,String> = transitions
                .associateBy { it["name"] as String }
                .mapValues { (_, value) ->
                    value["id"] as String
                }
        ctx[transitionsVar] = mapping
        return done()
    }
}