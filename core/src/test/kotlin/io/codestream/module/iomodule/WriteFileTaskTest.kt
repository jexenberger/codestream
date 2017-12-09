package io.codestream.module.iomodule

import io.codestream.module.coremodule.testId
import io.codestream.runtime.StreamContext
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.test.assertEquals

class WriteFileTaskTest {

    @Test
    fun testExec() {
        val ctx = StreamContext()
        val task = WriteFileTask()
        task.content = "hello world"
        task.encoding = "UTF-8"
        val tempFile = File.createTempFile(UUID.randomUUID().toString(), "test")
        tempFile.deleteOnExit()
        task.file = tempFile.absolutePath
        task.overwrite = true
        task.execute(testId(), ctx)
        assertEquals("hello world", tempFile.readText())
    }
}