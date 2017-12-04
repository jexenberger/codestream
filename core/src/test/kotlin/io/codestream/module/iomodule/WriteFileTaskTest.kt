package io.codestream.module.iomodule

import io.codestream.core.defaultCondition
import io.codestream.module.coremodule.createTaskContext
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.test.assertEquals

class WriteFileTaskTest {

    @Test
    fun testExec() {
        val (ctx, defn) = createTaskContext(IOModule(), "write", condition = defaultCondition())
        val task = WriteFileTask()
        task.content = "hello world"
        task.encoding = "UTF-8"
        val tempFile = File.createTempFile(UUID.randomUUID().toString(), "test")
        tempFile.deleteOnExit()
        task.file = tempFile.absolutePath
        task.overwrite = true
        task.execute(defn.id, ctx)
        assertEquals("hello world", tempFile.readText())
    }
}