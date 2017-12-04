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
        return TaskId("testGroup", "testStream", id)
    }


    @Test
    fun testInvalidModuleMsg() {
        val task = ExecutableDefinition(mockType, genId("2"), defaultCondition(), mapOf(Pair("willFail", "true")), 100)
        val invalidModuleMsg = task.invalidModuleMsg()
        assertTrue(invalidModuleMsg.contains("100"))
        assertTrue(invalidModuleMsg.contains("line"))

        val noLineTask = ExecutableDefinition(mockType, genId("2"), defaultCondition(), mapOf(Pair("willFail", "true")))
        assertFalse(noLineTask.invalidModuleMsg().contains("100"))
        assertFalse(noLineTask.invalidModuleMsg().contains("line"))
    }
}