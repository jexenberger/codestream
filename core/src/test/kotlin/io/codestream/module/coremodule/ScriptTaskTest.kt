package io.codestream.module.coremodule

import io.codestream.runtime.StreamContext
import org.junit.Test
import kotlin.test.assertEquals

class ScriptTaskTest {

    @Test
    fun testExec() {
        val ctx = StreamContext()
        val task = ScriptTask()
        task.script = "'hello' + ' ' + 'world'"
        task.outputVar = "hello_var"
        task.execute(testId(), ctx)
        assertEquals("hello world", ctx["hello_var"])
    }
}