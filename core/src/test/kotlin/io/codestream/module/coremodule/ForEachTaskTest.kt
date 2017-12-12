package io.codestream.module.coremodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.util.error
import io.codestream.util.events.Events
import io.codestream.util.ok
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ForEachTaskTest {

    private val mockType = TaskType("mock", "mockTask")

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
        ctx["x"] = true
        val taskId = genId("1")
        val stream = ForEachTask()
        stream.items = arrayOf(1, 2, 3)
        stream.iteratorVar = "theIterator"
        stream.currentValue = "theCurrentValue"
        val result = stream.before(taskId, ctx)
        assertTrue(result.ok(), result.error().toString())
        assertEquals(result.left!!, GroupTask.BeforeAction.Continue)
        assertEquals(1, ctx[stream.currentValue])
        assertTrue { ctx.containsKey(stream.iteratorVar) }
        stream.before(taskId, ctx)
        assertEquals(2, ctx[stream.currentValue])
        stream.before(taskId, ctx)
        assertEquals(3, ctx[stream.currentValue])
        val lastTime = stream.before(taskId, ctx)
        assertEquals(GroupTask.BeforeAction.Return, lastTime.left!!)
    }
}