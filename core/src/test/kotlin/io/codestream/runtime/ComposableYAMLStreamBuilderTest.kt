package io.codestream.runtime

import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ComposableYAMLStreamBuilderTest {


    @Test
    fun testBuild() {
        CodestreamRuntime.init(emptyArray(), force = true)
        val stream = ComposableYAMLStreamBuilder(File("src/test/resources/basic.yml")).build()
        assertEquals("aStream", stream.id)
        assertEquals("testGroup", stream.group)
        assertEquals("A really cool stream with relatively basic structure for test", stream.desc)
        assertEquals(2, stream.parameters.size)
        stream.parameters.entries.forEach {
            assertTrue { arrayOf("saying", "environment").contains(it.key) }
        }
        assertEquals(2, stream.runnables.size)
    }
}