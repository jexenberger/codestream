package io.codestream.module.iomodule

import io.codestream.module.coremodule.testId
import io.codestream.runtime.StreamContext
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
        val ctx = StreamContext()
        val task = ReadFileTask()
        task.encoding = "UTF-8"
        task.src = createTempFile.absolutePath
        task.outputVar = "file_content"
        task.execute(testId(), ctx)
        assertEquals("hello world", ctx["file_content"])
    }
}