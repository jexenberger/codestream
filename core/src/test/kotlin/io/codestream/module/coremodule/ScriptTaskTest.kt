package io.codestream.module.coremodule

import io.codestream.core.defaultCondition
import org.junit.Test
import kotlin.test.assertEquals

class ScriptTaskTest {

    @Test
    fun testExec() {
        val (ctx, defn) = createTaskContext(CoreModule(), "script", condition = defaultCondition())
        val task = ScriptTask()
        task.script = "'hello' + ' ' + 'world'"
        task.outputVar = "hello_var"
        task.execute(defn.id, ctx)
        assertEquals("hello world", ctx["hello_var"])
    }
}