package io.codestream.modules.atlassian

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.Server

abstract class BaseAtlassianTask : Task {

    @TaskProperty
    var config: String? = null

    var server: Server? = null

    protected val mapper = ObjectMapper().registerKotlinModule()


    abstract fun name(): String

    override final fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val jira = server?.let { it } ?: loadServer(config) ?: return invalidParameter(id, "No ${name()} config found")
        return runAgainstServer(id, ctx, jira)
    }

    abstract fun loadServer(config: String?): Server?

    abstract fun runAgainstServer(id: TaskId, ctx: StreamContext, server: Server): TaskError?

}