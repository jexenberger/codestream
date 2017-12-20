package io.codestream.runtime

import io.codestream.core.MockModule
import io.codestream.core.Module
import io.codestream.core.TaskId
import io.codestream.core.TaskType
import java.util.*

class StreamTest {

    private val mockType = TaskType("mock", "mockTask")

    init {
        Module += MockModule()
    }

    fun genId(id: String = UUID.randomUUID().toString()): TaskId {
        return TaskId("testGroup", "testStream", mockType, id)
    }

    /*
    @Test
    internal fun testRunWithFailure() {
        Events.set(MockEventProvider())
        val ctx = StreamContext()
        ctx["x"] = false
        val errorTask = ExecutableDefinition(mockType,genId("1"),false, null,mapOf(Pair("error","true")))
        val task = ExecutableDefinition(mockType,genId("2"),false, null,mapOf(Pair("willFail","true")))
        val stream = Stream("test", "group", "A test",  errorTask, mapOf())
        stream += task
        val result = stream.run(ctx)
        assertFalse { result.ok() }
        val ranTasks = stream.ranTasks
        assertEquals(0, ranTasks.size)
        assertEquals(1, stream.failedTasks.size)
        assertTrue(ctx["isErrorRan"] as Boolean)
    }*/

    /*
    @Test
    fun testResolveInput() {
        val stream = Stream("test", "group", "A test", null)
        val run = stream.run(StreamContext(), mapOf("inputVariable" to "hello world"))
        val message = if (!run) run.error!!.map { it.toString() }.toString() else ""
        assertTrue(message) { run.ok() }
    }*/

    /*
    @Test
    internal fun testRun() {
        Events.set(MockEventProvider())
        val ctx = StreamContext()
        ctx["x"] = false
        val stream = Stream("test", "group", "A test",  null, mapOf())
        val task1 = ExecutableDefinition(mockType,genId("1"))
        val task2 = ExecutableDefinition(mockType,genId("2"),false)
        val task3 = ExecutableDefinition(mockType,genId("3"),true, "x == true")
        val task4 = ExecutableDefinition(mockType,genId("4"),true, "!x")
        stream += task1
        stream += task2
        stream += task3
        stream += task4
        val result = stream.run(ctx)
        assertTrue { result.ok() }
        val ranTasks = stream.ranTasks
        assertEquals(3, ranTasks.size)
        assertEquals(1, stream.unrunTasks.size)
        for (task in stream.ranTasks) {
            assertTrue((task.value as MockTask).ran)
        }
        assertFalse((stream.unrunTasks[task3.id] as MockTask).ran)
    }*/


}