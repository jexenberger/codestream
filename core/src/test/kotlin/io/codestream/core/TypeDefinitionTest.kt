package io.codestream.core

import org.junit.Test
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TypeDefinitionTest {

    init {
        Module += MockModule()
    }

    private val mockType = TaskType("mock", "mockTask")

    fun genId(id: String = UUID.randomUUID().toString()): TaskId {
        return TaskId("testGroup", "testStream", mockType, id)
    }


    @Test
    fun testInvalidModuleMsg() {
        val id = genId("2")
        val binding = emptyBinding<MockTask>()
        val task = ExecutableDefinition<MockTask>(mockType, id, binding, defaultCondition(), 100)
        val invalidModuleMsg = task.invalidModuleMsg()
        assertTrue(invalidModuleMsg.contains("100"))
        assertTrue(invalidModuleMsg.contains("line"))

        val noLineTask = ExecutableDefinition<MockTask>(mockType, id, binding, defaultCondition())
        assertFalse(noLineTask.invalidModuleMsg().contains("100"))
        assertFalse(noLineTask.invalidModuleMsg().contains("line"))
    }
}