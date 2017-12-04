package io.codestream.module.iomodule

import io.codestream.core.defaultCondition
import io.codestream.module.coremodule.createTaskContext
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.test.assertTrue

class MkdirTaskTest {

    @Test
    fun testExec() {
        val createTempFile = File.createTempFile(UUID.randomUUID().toString(), "test")
        val dir = createTempFile.parent + "/${UUID.randomUUID()}"
        val (ctx, defn) = createTaskContext(IOModule(), "mkdir", condition = defaultCondition())
        val task = MkdirTask()
        task.dir = dir
        task.execute(defn.id, ctx)
        assertTrue(File(dir).isDirectory)
    }
}