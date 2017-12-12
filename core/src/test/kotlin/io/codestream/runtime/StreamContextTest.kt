package io.codestream.runtime

import io.codestream.module.coremodule.SampleClass
import io.codestream.util.YamlFactory
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class StreamContextTest {


    @Test
    fun testEvalTo() {
        val streamContext = testContext()
        val result: String? = streamContext.evalTo("\${test}")
        assertEquals("hello world", result)
    }

    private fun testContext(): StreamContext {
        val streamContext = StreamContext()
        val testClass = SampleClass()
        testClass.hello = "hello"
        testClass.world = "world"
        streamContext["test"] = "hello world"
        streamContext["testClass"] = testClass
        streamContext["anArray"] = arrayOf("hello", "world")
        streamContext["anIntArray"] = arrayOf(1, 2, 3, 4, 5)
        return streamContext
    }


    @Test
    fun testEvalDollarVars() {
        val ctx = testContext()
        val actual = ctx.evalScript<StreamContext>("\$ctx")
        assertEquals(ctx, actual)
        assertEquals("hello world", ctx.evalScript<String>("\$ctx.get('test')"))
        assertEquals("qwerty", ctx.evalScript<String>("\$ctx.set('x','qwerty');\$ctx.get('x')"))
        assertEquals(System.getenv(), ctx.evalScript("\$env"))
        assertEquals(System.getProperty("user.dir") + " hello", ctx.evalScript<String>("\$system.get('user.dir') + ' hello'"))
    }

    @Test
    fun testSubcontextParentDelegation() {
        val ctx = testContext()
        val subContext = ctx.subContext()
        assertNotNull(subContext.parent)
        assertEquals("hello world", subContext.evalScript<String>("test"))
    }


    @Test
    fun toYaml() {
        val ctx = testContext().subContext()
        ctx["\$\$qwerty"] = true
        ctx["\$\$qwerty2"] = "two"
        ctx["\$\$qwerty3"] = arrayOf(1, 2, 3, 4, 5)

        val yaml = ctx.toYaml()
        println(yaml)
    }

    @Test
    fun fromYaml() {
        val ctx = testContext().subContext()
        ctx["\$\$qwerty"] = true
        ctx["\$\$qwerty2"] = "two"
        ctx["\$\$qwerty3"] = arrayOf(1, 2, 3, 4, 5)

        val yaml = ctx.toYaml()
        val newCtx = StreamContext()
        val testCtx = StreamContext.fromYaml(yaml,newCtx)
        assertEquals(testCtx["\$\$qwerty1"], ctx["\$\$qwerty1"])
        assertEquals(testCtx["\$\$qwerty2"], ctx["\$\$qwerty2"])
        assertEquals(testCtx["test"], ctx["test"])
        assertEquals(testCtx["testClass"], ctx["testClass"])
    }



    @Test
    fun testFromMap() {
        val ctx = testContext()
        ctx["qwerty"] = "hello123"
        val yaml = ctx.toYaml()
        println(yaml)
        @Suppress("UNCHECKED_CAST")
        val map = YamlFactory.yaml().load(yaml) as Map<String, Any?>
        val newContext = StreamContext.fromMap(map)
        assertEquals("hello123", newContext["qwerty"])

    }

    @Test
    fun testFromMapExistingContext() {
        val ctx = testContext().subContext()
        ctx["qwerty"] = "hello123"
        val yaml = ctx.toYaml()
        println(yaml)
        @Suppress("UNCHECKED_CAST")
        val map = YamlFactory.yaml().load(yaml) as Map<String, Any?>
        val newContext = testContext().subContext()
        StreamContext.fromMap(map, newContext)
        assertEquals("hello123", newContext["qwerty"])

    }

}