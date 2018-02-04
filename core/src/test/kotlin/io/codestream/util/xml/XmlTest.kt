package io.codestream.util.xml

import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class XmlTest {


    @Test
    fun testParseFile() {
        val document = Xml.parse(File("src/test/resources/sample.xml"))
        assertNotNull(document)
    }

    @Test
    fun testParseString() {
        val document = Xml.parse(File("src/test/resources/sample.xml").readText())
        assertNotNull(document)
    }

    @Test
    fun testXPathSimple() {
        val document = Xml.parse(File("src/test/resources/sample.xml").readText())
        val result = Xml.eval(document, "//food/name")
        assertEquals("Belgian Waffles", result)
    }

}