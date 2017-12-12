package io.codestream.util.rest

import io.codestream.util.system
import java.io.File
import java.net.Authenticator
import java.net.InetSocketAddress
import java.net.PasswordAuthentication
import java.net.Proxy
import java.util.*

data class HttpProxy(val server: String,
                     val port: Int = 8080,
                     val user: String? = null,
                     val password: String? = null) {


    fun toProxy(): Proxy {
        if (user != null && password != null) {
            Authenticator.setDefault(object : Authenticator() {

                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(user, password.toCharArray())
                }
            })
        }
        return Proxy(Proxy.Type.HTTP, InetSocketAddress(server, port))
    }


    companion object {

        var defaultProxyPath = "${system.homeDir}/.cs/proxy.properties"

        var globalProxy: HttpProxy? = loadProxy()

        fun loadProxy(path: String? = null): HttpProxy? {
            val p = Properties()
            val fileToLoad = path?.let { it } ?: defaultProxyPath
            val file = File(fileToLoad)
            if (!file.exists()) {
                return null
            }
            p.load(file.reader())
            val server = p["server"]?.toString()
            val port = p["port"]?.toString()?.toInt() ?: 8080
            val user = p["user"]?.toString() ?: ""
            val password = p["password"]?.toString() ?: ""
            return server?.let { HttpProxy(it, port, user, password) }
        }

    }

}