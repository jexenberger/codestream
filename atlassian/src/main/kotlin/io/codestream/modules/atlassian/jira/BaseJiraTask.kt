package io.codestream.modules.atlassian.jira

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.codestream.core.*
import io.codestream.runtime.StreamContext

abstract class BaseJiraTask : Task, TaskBinder {

    @TaskProperty
    var config: String? = null

    var server:JiraServer? = null

    val mapper = ObjectMapper().registerKotlinModule()

    override final fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val configToUse = config?.let { it.trim() } ?: JiraServer.defaultPath
        val jira = server?.let { it } ?: JiraServer.load(configToUse) ?: return invalidParameter(id,"No Jira config found")
        return runAgainstJira(id, ctx, jira)
    }

    abstract fun runAgainstJira(id: TaskId, ctx: StreamContext, jira: JiraServer): TaskError?

}