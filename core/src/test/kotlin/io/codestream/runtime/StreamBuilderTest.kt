package io.codestream.runtime

import io.codestream.core.ExecutableDefinition
import io.codestream.core.Parameter
import io.codestream.core.TaskId
import io.codestream.core.TaskType
import io.codestream.module.coremodule.EchoTask
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class StreamBuilderTest {


    @Test
    fun testDefine() {
        CodestreamRuntime.init(emptyArray(), force = true)
        val stream = StreamBuilder("test", "group", "this is a test").define {
            input(Parameter(name = "input", desc = "desc", stringType = "string"))
            task(ExecutableDefinition<EchoTask>(
                    type = TaskType("core", "echo"),
                    id = TaskId("group", "test"),
                    params = mapOf("value" to "\${input}")
            ))
        }.stream
        assertNotNull(stream)
        assertNotNull(stream.runnables)
        val result = stream.run(mapOf("input" to "hello world"))
        assertNull(result, result?.toString())
    }
}