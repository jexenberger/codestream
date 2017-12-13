package io.codestream.util.rest

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.Proxy
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

class Request(val url: String, val path: String, var contentType: String = "application/json", val validateSSL: Boolean = true, val validateHostName: Boolean = true) {

    private val headers = mutableMapOf<String, String>()
    private var body: String = ""
    private val queryParms = mutableListOf<Pair<String, String>>()
    private var proxy: Proxy?

    init {
        proxy = HttpProxy.globalProxy?.toProxy()
    }


    fun proxy(server: String, port: Int = 8080, user: String? = null, password: String? = null): Request {
        proxy = HttpProxy(server, port, user, password).toProxy()
        return this
    }

    fun proxy(proxy: HttpProxy): Request {
        this.proxy = proxy.toProxy()
        return this
    }

    fun headers(vararg parms: Pair<String, String>): Request {
        for (parm in parms) {
            header(parm.first, parm.second)
        }
        return this
    }

    fun header(name: String, value: String): Request {
        headers[name] = value
        return this
    }

    fun parms(vararg parms: Pair<String, String>): Request {
        for (parm in parms) {
            queryParms.add(parm)
        }
        return this
    }

    fun parm(name: String, value: String): Request {
        queryParms.add(Pair(name, value))
        return this
    }

    fun body(content: String): Request {
        body = content
        return this
    }

    fun body(content: () -> String): Request {
        body(content())
        return this
    }

    fun post(): Response {
        val uri = url + "/" + path
        return doRequest(uri, "POST", { body })
    }

    fun put(): Response {
        val uri = url + "/" + path
        return doRequest(uri, "PUT", { body })
    }

    fun get(): Response {
        val uri = url + "/" + path + if (queryParms.isNotEmpty()) "?" + queryParms.map { "${it.first}=${it.second}" }.joinToString("&") else ""
        return doRequest(uri, "GET", { null })
    }

    private fun doRequest(uri: String, verb: String, body: () -> String?): Response {
        val url = URL(uri)
        val conn = (proxy?.let { url.openConnection(proxy) } ?: url.openConnection()) as HttpURLConnection

        if (conn is HttpsURLConnection && !validateSSL) {
            val sc = SSLContext.getInstance("SSL")
            sc.init(null, arrayOf(NoValidatingTrustManager), SecureRandom())
            conn.sslSocketFactory = sc.socketFactory
        }
        if (conn is HttpsURLConnection && !validateHostName) {
            conn.hostnameVerifier = HostnameVerifier { _, _ ->
                true
            }
        }
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", contentType)
        headers.forEach { k, v ->
            conn.setRequestProperty(k, v)
        }
        conn.requestMethod = verb
        val bodyStr = body()
        if (bodyStr != null) {
            conn.outputStream.write(bodyStr.toByteArray())
        }
        val responseCode = conn.responseCode
        val responseMessage: String = conn.responseMessage.let { it } ?: "<EMPTY>"
        val responseHeaders = conn.headerFields
        return try {
            val input = BufferedReader(InputStreamReader(conn.inputStream))
            val result = input.readText()
            Response(responseCode, responseMessage, result, responseHeaders)
        } catch (e: IOException) {
            Response(responseCode, responseMessage, conn.errorStream.bufferedReader().readText(), responseHeaders)
        }
    }

    companion object NoValidatingTrustManager : X509TrustManager {

        override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
        }

        override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate>? = emptyArray()
    }

    fun basicAuth(user: String, pwd: String): Request {
        val userNamePassword = Base64.getEncoder().encodeToString("$user:$pwd".toByteArray())
        return header(
                "Authorization",
                "Basic $userNamePassword"
        )
    }
}