package io.codestream.modules.atlassian

import com.fasterxml.jackson.module.kotlin.readValue
import io.codestream.modules.atlassian.jira.JiraServer
import io.codestream.util.YamlFactory
import io.codestream.util.system
import java.io.File

data class AtlassianTestSettings(val jiraUrl: String,
                                 val jiraUser: String,
                                 val jiraPassword: String,
                                 val jiraTestIssue: String,
                                 val jiraTestStatus: String,
                                 val jiraAssignmentUser: String) {


    val jiraServer: JiraServer
        get() = JiraServer(jiraUrl, jiraUser, jiraPassword)


    companion object {

        val defaultTestPath = "${system.homeDir}/.cstest/modules/atlassian/test.yaml"

        fun get(): AtlassianTestSettings {
            val file = File(defaultTestPath)
            if (!file.exists()) {
                throw RuntimeException("You need to create a test settings file @ $defaultTestPath")
            }
            return YamlFactory.mapper().readValue(file)
        }
    }


}