package io.codestream.module.utilmodule

import io.codestream.core.Parameter
import io.codestream.module.coremodule.testId
import io.codestream.resourcemodel.ResourceDefinition
import io.codestream.resourcemodel.ResourceDefinitions
import io.codestream.runtime.StreamContext
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

        val ctx = StreamContext()
        val task = YamlResourcesTask()
        task.file = "src/test/resources/resourcemodel/resources.yaml"
        task.execute(testId(), ctx)
        assertNotNull(ctx.resources)
        assertNotNull(ctx.resources["server1"])
    }
}