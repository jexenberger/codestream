package io.codestream.runtime.streamimpl

import io.codestream.core.Module
import io.codestream.core.TaskType
import io.codestream.runtime.CodestreamRuntime
import io.codestream.runtime.StreamContext
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class StreamModuleTest {

    @Test
    fun testInit() {
        CodestreamRuntime.init(emptyArray())
        val module = StreamModule("src/test/resources/modules/streammodule")
        Module += module
        assertEquals(1, module.factories.size)
        val parms = mapOf<String, Any?>(
                "simple" to "hello world"
        )
        val ctx = StreamContext()
        val taskType = TaskType("streammodule", "simple1")
        CodestreamRuntime.runtime.runTask(taskType, parms, ctx)?.let { fail(it.toMap().toString()) }
        assertEquals("hello world", ctx["res"])
        println(module.description)
        val documentation = module.documentation(taskType)
        println(documentation)

    }
}