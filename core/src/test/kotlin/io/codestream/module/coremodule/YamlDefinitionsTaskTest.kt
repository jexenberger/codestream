package io.codestream.module.coremodule

import io.codestream.core.defaultCondition
import io.codestream.resourcemodel.ResourceDefinitions
import org.junit.Test
import kotlin.test.assertNotNull

class YamlDefinitionsTaskTest {
    @Test
    fun testExec() {
        val (ctx, defn) = createTaskContext(CoreModule(), "yaml-resources", condition = defaultCondition())
        val task = YamlResourceDefinitionsTask()
        task.file = "src/test/resources/resourcemodel/resourcedefinitions.yaml"
        task.execute(defn.id, ctx)
        assertNotNull(ResourceDefinitions["server"])
    }
}