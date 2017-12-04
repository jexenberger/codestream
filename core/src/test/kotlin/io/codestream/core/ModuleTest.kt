package io.codestream.core

import org.junit.Test
import kotlin.test.assertNotNull

class ModuleTest {

    private val mockTaskId = TaskType("mock", "mockTask")
    private val id = TaskId("grp", "test")


    @Test
    fun testAddModule() {
        Module.clear()
        val module = MockModule()
        Module += module
        assertNotNull(Module[module.name])

    }
}