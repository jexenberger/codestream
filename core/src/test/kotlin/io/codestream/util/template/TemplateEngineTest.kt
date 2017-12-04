package io.codestream.util.template

import io.codestream.runtime.StreamContext
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.StringWriter
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TemplateEngineTest {

    private val data: StreamContext = StreamContext()

    @Before
    fun setUp() {
        TemplateEngine.reinit("src/test/resources")
        data["hello"] = "world"
    }

    @After
    fun tearDown() {
        TemplateEngine.reinit("templates")
    }

    @Test
    fun testRender() {
        val result = TemplateEngine.render("test", data)
        assertNotNull(result)
        assertEquals("hello world", result.trim())
    }

    @Test
    fun testRenderToWriter() {
        val target = StringWriter()
        TemplateEngine.render("test", target, data)
        assertEquals("hello world", target.toString().trim())
    }

    @Test
    fun testRenderInline() {
        val result = TemplateEngine.renderInline("hello {{hello}}", data)
        assertNotNull(result)
        assertEquals("hello world", result.trim())
    }


}