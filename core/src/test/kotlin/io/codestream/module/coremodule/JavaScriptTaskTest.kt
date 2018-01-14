package io.codestream.module.coremodule

import io.codestream.runtime.StreamContext
import org.junit.Test
import kotlin.test.assertEquals

class JavaScriptTaskTest {

    @Test
    fun testExec() {
        val ctx = StreamContext()
        val task = JavaScriptTask()
        task.script = "'hello' + ' ' + 'world'"
        task.outputVar = "hello_var"
        task.execute(testId(), ctx)
        assertEquals("hello world", ctx["hello_var"])
    }
}