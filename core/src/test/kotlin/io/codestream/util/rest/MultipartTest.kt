package io.codestream.util.rest

import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import kotlin.test.assertEquals

class MultipartTest {

    @Test
    fun testWrite() {
        val multipart = Multipart(File("src/test/resources/nested.yml"), "application/x-yaml")
        val bos = ByteArrayOutputStream()
        multipart.write(bos, UUID.randomUUID().toString())
        println(bos.toString())
    }

    @Test
    fun testWriteMany() {
        val part = Multipart(File("src/test/resources/nested.yml"), "application/x-yaml")
        val byteArrayOutputStream = ByteArrayOutputStream()
        Multipart.write(byteArrayOutputStream, "UTF-8", Multipart.getId(), arrayOf(part))
        println(byteArrayOutputStream.toString())
    }

    @Test
    fun testGetFileType() {
        assertEquals("application/x-yaml", Multipart.contentTypeFromFile(File("src/test/resources/test_layout.yaml")))
        assertEquals("application/x-yaml", Multipart.contentTypeFromFile(File("src/test/resources/test_layout.yml")))
        assertEquals("application/pdf", Multipart.contentTypeFromFile(File("src/test/resources/test_layout.pdf")))
        assertEquals("application/xml", Multipart.contentTypeFromFile(File("src/test/resources/test_layout.xml")))
        assertEquals("application/octet-stream", Multipart.contentTypeFromFile(File("src/test/resources/test_layout.qweqwe")))
    }
}