package io.codestream.module.iomodule

import io.codestream.core.defaultCondition
import io.codestream.module.coremodule.createTaskContext
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.test.assertEquals

class ReadFileTaskTest {

    @Test
    fun testExec() {
        val createTempFile = File.createTempFile(UUID.randomUUID().toString(), "test")
        createTempFile.deleteOnExit()
        createTempFile.writeText("hello world")
        val (ctx, defn) = createTaskContext(IOModule(), "delete", condition = defaultCondition())
        val task = ReadFileTask()
        task.encoding = "UTF-8"
        task.src = createTempFile.absolutePath
        task.outputVar = "file_content"
        task.execute(defn.id, ctx)
        assertEquals("hello world", ctx["file_content"])
    }
}