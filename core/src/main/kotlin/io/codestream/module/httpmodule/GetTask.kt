package io.codestream.module.httpmodule

import io.codestream.core.TaskDescriptor
import io.codestream.util.rest.Request

@TaskDescriptor(name = "get", description = "Performs an HTTP GET operation")
class GetTask : BaseHttpTask() {


    override fun handleRequest(request: Request) = request.get()
}