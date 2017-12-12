package io.codestream.modules.atlassian.jira

import io.codestream.util.system
import java.io.File
import java.util.*

data class JiraServer(val url:String, val user:String, val pwd:String) {



    companion object {

        val defaultPath = "${system.homeDir}/.cs/modules/atlassian/jira/jira.properties"

        fun load() = load(defaultPath)

        fun load(path: String? = null): JiraServer? {
            val p = Properties()
            val fileToLoad = path?.let { it } ?: defaultPath
            val file = File(fileToLoad)
            if (!file.exists()) {
                return null
            }
            p.load(file.reader())
            val url = p["url"]?.toString()
            val user = p["user"]?.toString() ?: ""
            val pwd = p["pwd"]?.toString() ?: ""
            return url?.let { JiraServer(it, user, pwd) }
        }
    }

}
