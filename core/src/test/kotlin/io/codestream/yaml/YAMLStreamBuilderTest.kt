package io.codestream.yaml

import io.codestream.core.Module
import io.codestream.module.coremodule.CoreModule
import io.codestream.runtime.CodestreamRuntime
import io.codestream.runtime.StreamContext
import io.codestream.runtime.YAMLStreamBuilder
import org.junit.Test
import org.yaml.snakeyaml.Yaml
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
    fun testLayout() {
        val yaml = Yaml().load(File("src/test/resources/test_layout.yaml").reader()) as Map<String, Any>
        println(yaml["tasks"])
    }

    @Test
    fun testInitFromFile() {
        val builder = YAMLStreamBuilder(File("src/test/resources/sample.yml"))
        assertNotNull(builder.data)
    }

    @Test
    fun testBuild() {
        CodestreamRuntime.init(emptyArray(), force = true)
        val builder = YAMLStreamBuilder(File("src/test/resources/basic.yml"))
        val stream = builder.build()
        assertNotNull(stream)
        //assertEquals(3, stream.module.size)
        assertEquals(2, stream.parameters.size)
        Module += CoreModule()
        val result = stream.run(mapOf(
                "saying" to "#{\$os.user}",
                "environment" to "uat"
        ))
        assertNull(result, result?.toString())
    }

    @Test
    fun testNested() {
        CodestreamRuntime.init(emptyArray(), force = true)
        val builder = YAMLStreamBuilder(File("src/test/resources/sample-nested-tasks.yaml"))
        val stream = builder.build()
        assertNotNull(stream)
    }

    @Test
    fun testBuild2() {
        CodestreamRuntime.init(emptyArray(), force = true)
        val builder = YAMLStreamBuilder(File("src/test/resources/basic.yml"))
        val stream = builder.build()
        assertNotNull(stream)
        //assertEquals(3, stream.module.size)
        assertEquals(2, stream.parameters.size)
        Module += CoreModule()
        val ctx = StreamContext()
        ctx.log.debug = true
        val result = stream.run(mapOf(
                "saying" to "#{\$os.user}",
                "environment" to "uat"
        ), ctx)
        assertNull(result, result?.toString())
    }
}