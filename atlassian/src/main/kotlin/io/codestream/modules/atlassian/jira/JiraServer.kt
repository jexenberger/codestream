package io.codestream.modules.atlassian.jira

import io.codestream.util.ServerConfig
import io.codestream.util.system

object JiraServer : ServerConfig {
    override val defaultPath: String
        get() = "${system.homeDir}/.cs/modules/atlassian/jira.properties"

}
