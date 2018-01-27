package io.codestream.util.rest

import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.Proxy
import java.net.URL
import java.nio.charset.Charset
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

class Request(val uri: String,
              var contentType: String = "application/json",
              val validateSSL: Boolean = true,
              val validateHostName: Boolean = true,
              val encoding: String = "UTF-8") {

    constructor(url: String,
                path: String,
                contentType: String,
                validateSSL: Boolean,
                validateHostName: Boolean) : this(url + "/" + path, contentType, validateSSL, validateHostName)


    private val headers = mutableMapOf<String, String>()
    private var body: String = ""
    private val queryParms = mutableListOf<Pair<String, String>>()
    private var proxy: Proxy?
    private var outputFile: File? = null
    private val attachments: MutableList<Multipart> = mutableListOf()

    init {
        proxy = HttpProxy.globalProxy?.toProxy()
    }


    fun proxy(server: String, port: Int = 8080, user: String? = null, password: String? = null): Request {
        proxy = HttpProxy(server, port, user, password).toProxy()
        return this
    }

    fun attachment(file: File, contentType: String = Multipart.contentTypeFromFile(file), encoding: String = "UTF-8"): Request {
        attachments += Multipart(file, contentType, encoding)
        return this
    }

    fun proxy(proxy: HttpProxy): Request {
        this.proxy = proxy.toProxy()
        return this
    }

    fun outputFile(outputFile: File): Request {
        this.outputFile = outputFile
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
        return doRequest(uri, "POST", { body })
    }

    fun put(): Response {
        return doRequest(uri, "PUT", { body })
    }

    fun delete(): Response {
        return doRequest(uri, "DELETE", { body })
    }

    fun get(): Response {
        return doRequest(uri, "GET", { null })
    }

    private fun writeAttachments(conn: HttpURLConnection) {
        val id = Multipart.getId()
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=$id")
        Multipart.write(
                stream = conn.outputStream,
                encoding = this.encoding,
                parts = attachments.toTypedArray(),
                id = id
        )
    }

    private fun writeBody(body: () -> String?, conn: HttpURLConnection) {
        val bodyStr = body()
        if (bodyStr != null) {
            conn.outputStream.write(bodyStr.toByteArray(Charset.forName(encoding)))
        }
    }

    fun doRequest(uri: String, verb: String, body: () -> String?): Response {
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
        if (attachments.isNotEmpty() && !"GET".equals(verb)) {
            writeAttachments(conn)
        } else {
            writeBody(body, conn)
        }
        val responseCode = conn.responseCode
        val responseMessage: String = conn.responseMessage.let { it } ?: "<EMPTY>"
        val responseHeaders = conn.headerFields
        return try {
            val result: String = if (outputFile != null) {
                conn.inputStream.copyTo(outputFile!!.outputStream())
                outputFile!!.absolutePath
            } else {
                val input = conn.inputStream.bufferedReader()
                input.readText()
            }
            Response(responseCode, responseMessage, result, responseHeaders)
        } catch (e: IOException) {
            val errorText = try {
                conn.errorStream.bufferedReader().readText()
            } catch (e: IllegalStateException) {
                "<unknown>"
            }
            Response(responseCode, responseMessage, errorText, responseHeaders)
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