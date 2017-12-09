package io.codestream.module.coremodule

import io.codestream.core.TaskId
import io.codestream.runtime.StreamContext
import org.junit.Test

class WhileTaskTest {


    @Test
    fun testExec() {
        val ctx = StreamContext()
        val id = TaskId("test", "test")
        val task = WhileTask()
        task.before(id, ctx)
        task.after(id, ctx)
    }
}