package io.codestream.module.utilmodule

import io.codestream.module.coremodule.testId
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