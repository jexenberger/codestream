package io.codestream.util

import io.codestream.runtime.StreamContext
import org.junit.Test
import kotlin.test.assertEquals

class EvalTest {

    @Test
    fun testEval() {
        val result = Eval.eval<Boolean>("x > 0", mapOf(Pair("x", 1)))
        assertEquals(true, result)
    }

    @Test
    fun testEvalStreamContext() {
        val ctx = StreamContext()
        ctx["test"] = "hello"
        val result = Eval.eval<String>("test", ctx)
        assertEquals("hello", result)
    }
}