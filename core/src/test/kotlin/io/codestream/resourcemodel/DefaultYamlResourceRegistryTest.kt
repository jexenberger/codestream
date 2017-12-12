package io.codestream.resourcemodel

import io.codestream.core.Parameter
import org.junit.Before
import org.junit.Test
import kotlin.test.*

class DefaultYamlResourceRegistryTest {

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
                ),
                Parameter(
                        name = "hostname",
                        stringType = "string",
                        desc = "Server host name"
                ),
                Parameter(
                        name = "cores",
                        desc = "number of cores on the machine",
                        stringType = "int"
                ),
                Parameter(
                        name = "users",
                        desc = "Users on the machine",
                        stringType = "[string]"
                )

        ))
    }

    @Test
    fun testInit() {
        val defaultYamlResourceRegistry = DefaultYamlResourceRegistry("src/test/resources/resourcemodel/resources.yaml")
        val load = defaultYamlResourceRegistry.load()
        assertNull(load?.errors?.joinToString())
        val resource = defaultYamlResourceRegistry["server1"]
        assertNotNull(resource)
        assertEquals("196.168.1.1", resource?.get("ip"))
        assertEquals("server01", resource?.get("hostname"))
        assertEquals("fqdn.com", resource?.get("dns"))
        assertEquals(4, resource?.get("cores"))
        resource?.get("users")?.let {
            @Suppress("UNCHECKED_CAST")
            val userList = it as Array<String>
            assertTrue(userList.contains("jack01"))
            assertTrue(userList.contains("john01"))
            assertTrue(userList.contains("jill01"))
        } ?: fail("users should have been present")
    }

    @Test
    fun testFind() {
        val defaultYamlResourceRegistry = DefaultYamlResourceRegistry("src/test/resources/resourcemodel/resources.yaml")
        defaultYamlResourceRegistry.load()
        var result = defaultYamlResourceRegistry.find("cores" to 2)
        assertEquals(1, result.size)

        result = defaultYamlResourceRegistry.find("hostname" to "server01")
        assertEquals(1, result.size)

        result = defaultYamlResourceRegistry.find(
                "hostname" to "server02",
                "cores" to 2
        )
        assertEquals(1, result.size)
    }

    @Test
    fun testFindNegative() {
        val defaultYamlResourceRegistry = DefaultYamlResourceRegistry("src/test/resources/resourcemodel/resources.yaml")
        defaultYamlResourceRegistry.load()
        //doesnt match
        var result = defaultYamlResourceRegistry.find("cores" to 3)
        assertEquals(0, result.size)

        //no matches
        result = defaultYamlResourceRegistry.find()
        assertEquals(2, result.size)

        //only partial match
        result = defaultYamlResourceRegistry.find(
                "hostname" to "server02",
                "cores" to 3
        )
        assertEquals(0, result.size)
    }
}