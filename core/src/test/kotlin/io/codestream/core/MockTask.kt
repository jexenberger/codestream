package io.codestream.core

import io.codestream.resourcemodel.Resource
import io.codestream.runtime.StreamContext
import javax.validation.constraints.NotNull

class MockTask() : Task, TaskBinder {


    var ran: Boolean = false
    var inputVariable: String? = null

    @TaskProperty
    @get:NotNull
    var testSet: String? = null


    @TaskProperty
    var server: Resource? = null

    @TaskProperty
    var testTwo: Int? = null

    @TaskProperty
    var willFail: Boolean? = null

    @TaskProperty
    var throwException: Boolean? = null

    @TaskProperty
    var error: Boolean? = null

    @TaskProperty
    var list: Array<String>? = null

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        println(this)
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