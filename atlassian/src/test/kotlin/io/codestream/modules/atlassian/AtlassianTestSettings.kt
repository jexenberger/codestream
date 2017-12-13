package io.codestream.modules.atlassian

import com.fasterxml.jackson.module.kotlin.readValue
import io.codestream.util.Server
import io.codestream.util.YamlFactory
import io.codestream.util.system
import java.io.File

data class AtlassianTestSettings(val jiraUrl: String,
                                 val jiraUser: String,
                                 val jiraPassword: String,
                                 val jiraTestIssue: String,
                                 val jiraTestStatus: String,
                                 val jiraAssignmentUser: String,
                                 val bitbucketUrl: String,
                                 val bitbucketUser: String,
                                 val bitbucketPassword: String,
                                 val bitbucketSourceBranch: String,
                                 val bitbucketTargetBranch: String,
                                 val bitbucketReviewer: String,
                                 val bitbucketProject: String,
                                 val bitbucketRepo: String) {


    val jiraServer: Server
        get() = Server(jiraUrl, jiraUser, jiraPassword)

    val bitbucketServer: Server
        get() = Server(bitbucketUrl, bitbucketUser, bitbucketPassword)


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