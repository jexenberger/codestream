package io.codestream.module.coremodule

import io.codestream.core.Parameter
import io.codestream.core.defaultCondition
import io.codestream.resourcemodel.ResourceDefinition
import io.codestream.resourcemodel.ResourceDefinitions
import org.junit.Test
import kotlin.test.assertNotNull

class YamlResourcesTaskTest {

    @Test
    fun testExec() {
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

        val (ctx, defn) = createTaskContext(CoreModule(), "yaml-resources", condition = defaultCondition())
        val task = YamlResourcesTask()
        task.file = "src/test/resources/resourcemodel/resources.yaml"
        task.execute(defn.id, ctx)
        assertNotNull(ctx.resources)
        assertNotNull(ctx.resources["server1"])
    }
}