package io.codestream.modules.atlassian

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.Server
import io.codestream.util.json.json

abstract class BaseAtlassianTask : Task {

    @TaskProperty(description = "Path to the server configuration")
    var config: String? = null

    var server: Server? = null

    protected val mapper = json


    abstract fun name(): String

    override final fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val jira = server?.let { it } ?: loadServer(config) ?: return invalidParameter(id, "No ${name()} config found")
        return runAgainstServer(id, ctx, jira)
    }

    abstract fun loadServer(config: String?): Server?

    abstract fun runAgainstServer(id: TaskId, ctx: StreamContext, server: Server): TaskError?

}