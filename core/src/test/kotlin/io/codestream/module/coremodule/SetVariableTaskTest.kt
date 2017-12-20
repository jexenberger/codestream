package io.codestream.module.coremodule

import io.codestream.runtime.StreamContext
import org.junit.Test
import kotlin.test.assertEquals

class SetVariableTaskTest {
    @Test
    fun testExecute() {
        val ctx = StreamContext()
        ctx["hello"] = "world"
        val setVariable = SetVariableTask()
        setVariable.outputVar = "test"
        setVariable.varType = "int"
        setVariable.value = "\${hello}"
        setVariable.execute(testId(), ctx)
        assertEquals("world", ctx["test"])
    }
}