package io.codestream.yaml

import io.codestream.core.Module
import io.codestream.module.coremodule.CoreModule
import io.codestream.runtime.CodestreamRuntime
import io.codestream.runtime.YAMLStreamBuilder
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class YAMLStreamBuilderTest {


    @Test
    fun testInit() {

        val text = """
    |hello: world
    |myNameIs: Slim shady
    |stuffs:
    |   - one
    |   - two
    |complex:
    |   - ctest: one
    |     ctest2: two
    """.trimMargin()


        val builder = YAMLStreamBuilder(text)
        assertNotNull(builder.yaml)
        assertNotNull(builder.data)
    }

    @Test
    fun testInitFromFile() {
        val builder = YAMLStreamBuilder(File("src/test/resources/sample.yml"))
        assertNotNull(builder.data)
    }

    @Test
    fun testBuild() {
        CodestreamRuntime.init(emptyArray(), force = true)
        val builder = YAMLStreamBuilder(File("src/test/resources/sample.yml"))
        val stream = builder.build()
        assertNotNull(stream)
        //assertEquals(3, stream.module.size)
        assertEquals(1, stream.parameters.size)
        Module += CoreModule()
        val result = stream.run(mapOf(Pair("saying", "#{\$os.user}")))
        assertNull(result, result?.toString())
    }

    @Test
    fun testBuild2() {
        CodestreamRuntime.init(emptyArray(), force = true)
        val builder = YAMLStreamBuilder(File("src/test/resources/sample.yml"))
        val stream = builder.build()
        assertNotNull(stream)
        //assertEquals(3, stream.module.size)
        assertEquals(1, stream.parameters.size)
        Module += CoreModule()
        val result = stream.run(mapOf(Pair("saying", "#{\$os.user}")))
        assertNull(result, result?.toString())
    }
}