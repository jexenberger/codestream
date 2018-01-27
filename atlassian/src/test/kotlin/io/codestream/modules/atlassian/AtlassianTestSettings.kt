package io.codestream.modules.atlassian

import io.codestream.util.Server

data class AtlassianTestSettings(val jiraUrl: String = "http://localhost:4444",
                                 val jiraUser: String = "test",
                                 val jiraPassword: String = "pwd",
                                 val jiraTestIssue: String = "COS-123",
                                 val jiraTestStatus: String = "status",
                                 val jiraAssignmentUser: String = "adrian",
                                 val bitbucketUrl: String = "http://localhost:4444",
                                 val bitbucketUser: String = "bb_test",
                                 val bitbucketPassword: String = "bb_pwd",
                                 val bitbucketSourceBranch: String = "brn",
                                 val bitbucketTargetBranch: String = "target",
                                 val bitbucketReviewer: String = "qwerty",
                                 val bitbucketProject: String = "proj01",
                                 val bitbucketRepo: String = "repo01",
                                 val bitbucketBasePath: String? = "bitbucket") {


    val jiraServer: Server
        get() = Server(jiraUrl, jiraUser, jiraPassword)

    val bitbucketServer: Server
        get() = Server(bitbucketUrl, bitbucketUser, bitbucketPassword)


    companion object {
        fun get() = AtlassianTestSettings()
    }


}