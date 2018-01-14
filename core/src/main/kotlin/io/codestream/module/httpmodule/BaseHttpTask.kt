package io.codestream.module.httpmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.rest.Request
import io.codestream.util.rest.Response
import org.apache.bval.constraints.NotEmpty
import java.io.File

abstract class BaseHttpTask : Task, SetOutput {
    @TaskProperty(description = "URI to call")
    @get:NotEmpty
    var uri: String = ""
    @TaskProperty(description = "Headers for the HTTP operation")
    var headers: Map<String, Any?> = mapOf()
    @TaskProperty(description = "Name of variable to set in context, is set to the file path, it outputFile is specified")
    override var outputVar: String = "\$httpGet"
    @TaskProperty(description = "Path of an output file to dump result to")
    var outputFile: File? = null
    @TaskProperty(description = "Validate SSL connection")
    var validateSSL: Boolean = true
    @TaskProperty(description = "Validate Hostname on SSL connection")
    var validateHostName: Boolean = true
    @TaskProperty(description = "Proxy server to use for the request (can be set in system or globally in ~/.cs/proxy.settings)")
    var proxyHost: String? = null
    @TaskProperty(description = "Proxy user to use for the request (can be set in system or globally in ~/.cs/proxy.settings)")
    var proxyUser: String? = null
    @TaskProperty(description = "Proxy password to use for the request (can be set in system or globally in ~/.cs/proxy.settings)")
    var proxyPassword: String? = null
    @TaskProperty(description = "Proxy port to use for the request (can be set in system or globally in ~/.cs/proxy.settings)")
    var proxyPort: Int = 8080
    @TaskProperty(description = "Variable to set HTTP response code in context")
    @get:NotEmpty
    var httpStatusVar: String = "\$httpStatus"
    @TaskProperty(description = "Variable to set HTTP response message in context")
    @get:NotEmpty
    var httpResponseMessageVar: String = "\$httpResponseMessage"
    @TaskProperty(description = "Variable to set HTTP response headers in context")
    @get:NotEmpty
    var httpResponseHeadersVar: String = "\$httpResponseHeadersVar"


    protected abstract fun handleRequest(request: Request): Response

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val request = Request(uri = uri,
                validateSSL = validateSSL,
                validateHostName = validateHostName)
        headers.forEach { t, u -> request.header(t, u?.toString() ?: "") }
        proxyHost?.let { request.proxy(it, proxyPort, proxyUser, proxyPassword) }
        outputFile?.let { request.outputFile(it) }
        val (status, responseMessage, body, responseHeaders) = handleRequest(request)
        ctx[httpStatusVar] = status
        ctx[httpResponseMessageVar] = responseMessage
        ctx[outputVar] = body
        ctx[httpResponseHeadersVar] = responseHeaders
        return done()
    }
}