package io.codestream.module.httpmodule

import io.codestream.core.TaskProperty
import io.codestream.util.rest.Request
import io.codestream.util.rest.Response
import java.io.File
import javax.validation.constraints.NotEmpty

abstract class BaseBodiedTask : BaseHttpTask() {
    @TaskProperty(description = "Body to submit with request")
    @get:NotEmpty
    val body: String = ""

    @TaskProperty(description = "List of files to upload with the request")
    val attachments: Array<File> = emptyArray()


    override fun handleRequest(request: Request): Response {
        request.body(body)
        attachments.forEach { request.attachment(it) }
        return invoke(request)
    }

    abstract fun invoke(request: Request): Response
}