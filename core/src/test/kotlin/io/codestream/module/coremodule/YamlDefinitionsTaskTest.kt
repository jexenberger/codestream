package io.codestream.module.coremodule

import io.codestream.resourcemodel.ResourceDefinitions
import io.codestream.runtime.StreamContext
import org.junit.Test
import kotlin.test.assertNotNull

class YamlDefinitionsTaskTest {
    @Test
    fun testExec() {
        val ctx = StreamContext()
        val task = YamlResourceDefinitionsTask()
        task.file = "src/test/resources/resourcemodel/resourcedefinitions.yaml"
        task.execute(testId(), ctx)
        assertNotNull(ResourceDefinitions["server"])
    }
}