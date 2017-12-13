package io.codestream.modules.atlassian.bitbucket

import io.codestream.util.ServerConfig
import io.codestream.util.system

object BitBucketServer : ServerConfig {


    override val defaultPath: String
        get() = "${system.homeDir}/.cs/modules/atlassian/bitbucket.properties"
}