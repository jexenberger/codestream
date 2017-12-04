package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.events.Events
import org.junit.Test
import java.util.*

class GroupingTaskTest {

    private val mockType = TaskType("mock", "mockTask")
    private val groupType = TaskType("core", "group")

    init {
        Module += MockModule()
    }

    fun genId(id: String = UUID.randomUUID().toString()): TaskId {
        return TaskId("testGroup", "testStream", id)
    }

    @Test
    fun testExecute() {
        Events.set(MockEventProvider())
        val ctx = StreamContext()
        ctx["x"] = false
        val task1 = ExecutableDefinition(mockType, genId("1"))
        val task2 = ExecutableDefinition(mockType, genId("2"))
        val task3 = ExecutableDefinition(mockType, genId("3"), scriptCondition("x == true"))
        val task4 = ExecutableDefinition(mockType, genId("4"), scriptCondition("!x"))
        val tasks = arrayOf(task1, task2, task3, task4)
    }
}