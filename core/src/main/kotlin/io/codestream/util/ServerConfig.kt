package io.codestream.util

import java.io.File
import java.util.*

interface ServerConfig {

    val defaultPath: String

    fun load() = load(defaultPath)

    fun load(path: String? = null): Server? {
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
        return url?.let { Server(it, user, pwd) }
    }

}