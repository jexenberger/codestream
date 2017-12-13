package io.codestream.modules.atlassian.jira

import io.codestream.modules.atlassian.BaseAtlassianTask
import io.codestream.util.Server

abstract class BaseJiraTask : BaseAtlassianTask() {
    override fun name(): String {
        return "Jira"
    }

    override fun loadServer(config: String?): Server? {
        return JiraServer.load(config)
    }
}