package io.codestream.resourcemodel

import io.codestream.util.ok
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DefaultYamlResourceDefinitionsTest {

    @Test
    fun testLoad() {
        val result = DefaultYamlResourceDefinitions("src/test/resources/resourcemodel/resourcedefinitions.yaml").load()
        assert(result.ok(), { "${result.right?.errors}" })
        assertNotNull(ResourceDefinitions["server"])
        val registry = DefaultYamlResourceRegistry("src/test/resources/resourcemodel/resources.yaml")
        val registryLoadResult = registry.load()
        assertNull(registryLoadResult)
        assertNotNull(registry["server1"])
        assertNotNull(registry["server2"])
    }
}