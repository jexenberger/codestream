package io.codestream.module.httpmodule

import io.codestream.core.TaskDescriptor
import io.codestream.util.rest.Request
import io.codestream.util.rest.Response

@TaskDescriptor(name = "get", description = "Performs an HTTP PUT operation")
class PutTask : BaseBodiedTask() {

    override fun invoke(request: Request): Response {
        return request.put()
    }
}