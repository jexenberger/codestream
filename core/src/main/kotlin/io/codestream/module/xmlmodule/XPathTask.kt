package io.codestream.module.xmlmodule

import io.codestream.core.SetOutput
import io.codestream.core.Task
import io.codestream.core.TaskError
import io.codestream.core.TaskId
import io.codestream.runtime.StreamContext

class XPathTask : Task, SetOutput {

    override var outputVar: String = ""


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}