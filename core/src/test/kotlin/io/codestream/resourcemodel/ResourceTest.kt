package io.codestream.resourcemodel

import io.codestream.core.Parameter
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ResourceTest {


    @Before
    fun before() {
        ResourceDefinitions["server"] = ResourceDefinition("server", "Simple server definition", arrayOf(
                Parameter(
                        name = "dns",
                        stringType = "string",
                        desc = "fully qualified domain name"
                ),
                Parameter(
                        name = "ip",
                        stringType = "string",
                        desc = "IP Address"
                )
        ))
    }


    @Test
    fun testEntries() {
        val defn = ResourceDefinitions["server"]!!
        val res = Resource("test", "server", defn, mapOf<String, Any?>("dns" to "dns.com"))
        assertEquals("test", res["id"])
        assertEquals("server", res["type"])
        assertEquals(defn, res["defn"])
        assertEquals("dns.com", res["dns"])
    }
}