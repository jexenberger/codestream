package io.codestream.module.coremodule

import io.codestream.runtime.StreamContext
import org.junit.Test
import kotlin.test.assertEquals

class SetVariableTaskTest {
    @Test
    fun testExecute() {
        val ctx = StreamContext()
        val setVariable = SetVariableTask()
        setVariable.outputVar = "test"
        setVariable.value = "world"
        setVariable.execute(testId(), ctx)
        assertEquals("world", ctx["test"])
    }
}