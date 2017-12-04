package io.codestream.module.coremodule

import io.codestream.core.defaultCondition
import org.junit.Test
import kotlin.test.assertEquals

class SetVariableTaskTest {
    @Test
    fun testExecute() {
        val (ctx, defn) = createTaskContext(CoreModule(), "set", condition = defaultCondition())
        ctx["hello"] = "world"
        val setVariable = SetVariableTask()
        setVariable.name = "test"
        setVariable.varType = "int"
        setVariable.value = "\${hello}"
        setVariable.execute(defn.id, ctx)
        assertEquals("world", ctx["test"])
    }
}