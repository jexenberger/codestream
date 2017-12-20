package io.codestream.modules.atlassian.bitbucket

import io.codestream.core.TaskProperty
import io.codestream.modules.atlassian.BaseAtlassianTask
import io.codestream.util.Server

abstract class BaseBitbucketTask : BaseAtlassianTask() {

    @TaskProperty(description = "Base API path")
    var basePath = "rest/api/1.0"

    override fun name(): String {
        return "BitBucket"
    }

    override fun loadServer(config: String?): Server? {
        return BitBucketServer.load(config)
    }
}