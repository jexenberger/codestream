package io.codestream.module.coremodule

import io.codestream.runtime.StreamContext
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.test.assertEquals

class RestoreTaskTest {

    @Test
    fun testExecuteFileExistsAndNoOverwrite() {
        val ctx = StreamContext()
        ctx["hello"] = "world_restore"
        val file = File(System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID())
        file.writeText(ctx.toYaml())
        file.deleteOnExit()
        val restoreTask = RestoreTask()
        restoreTask.execute(testId(), ctx)
        assertEquals("world_restore", ctx["hello"])
    }
}