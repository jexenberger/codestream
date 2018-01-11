package io.codestream.runtime

import io.codestream.core.*
import io.codestream.module.coremodule.EchoTask
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class StreamBuilderTest {


    @Test
    fun testDefine() {
        CodestreamRuntime.init(emptyArray(), force = true)
        val type = TaskType("core", "echo")
        val stream = StreamBuilder("test", "group", "this is a test").define {
            input(Parameter(name = "input", desc = "desc", stringType = "string"))
            task(ExecutableDefinition<EchoTask>(
                    type = type,
                    id = TaskId("group", "test", type),
                    binding = emptyBinding()
            ))
        }.stream
        assertNotNull(stream)
        assertNotNull(stream.runnables)
        val result = stream.run(mapOf("input" to "hello world"))
        assertNull(result, result?.toString())
    }
}