package io.codestream.module.coremodule

import io.codestream.core.defaultCondition
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.test.assertEquals

class RestoreTaskTest {

    @Test
    fun testExecuteFileExistsAndNoOverwrite() {
        val (ctx, defn) = createTaskContext(CoreModule(), "exec", condition = defaultCondition())
        ctx["hello"] = "world_restore"
        val file = File(System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID())
        file.writeText(ctx.toYaml())
        file.deleteOnExit()
        val restoreTask = RestoreTask()
        restoreTask.execute(defn.id, ctx)
        assertEquals("world_restore", ctx["hello"])
    }
}