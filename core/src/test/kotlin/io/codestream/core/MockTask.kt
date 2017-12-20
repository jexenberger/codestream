package io.codestream.core

import io.codestream.resourcemodel.Resource
import io.codestream.runtime.StreamContext
import javax.validation.constraints.NotNull

@TaskDescriptor("mock", description = "A Mock task for testing")
class MockTask : Task {


    var ran: Boolean = false
    var inputVariable: String? = null

    @TaskProperty(description = "testSet")
    @get:NotNull
    var testSet: String? = null


    @TaskProperty(description = "server")
    var server: Resource? = null

    @TaskProperty(description = "testTwo")
    var testTwo: Int? = null

    @TaskProperty(description = "willFail")
    var willFail: Boolean? = null

    @TaskProperty(description = "throwException")
    var throwException: Boolean? = null

    @TaskProperty(description = "error")
    var error: Boolean? = null

    @TaskProperty(description = "array")
    var list: Array<String>? = null

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        Thread.sleep(200)
        server?.let { ctx["resourceName"] = it.id }
        inputVariable = ctx["inputVariable"] as String?
        ran = true
        if (willFail != null && willFail == true) {
            return TaskError(id, "TestFail", "set to fail")
        }
        if (throwException != null && throwException == true) {
            throw RuntimeException()
        }
        if (error == true) {
            ctx["isErrorRan"] = true
        }
        ctx["isRan"] = true
        return done()
    }


}