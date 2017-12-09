package io.codestream.module.iomodule

import io.codestream.module.coremodule.testId
import io.codestream.runtime.StreamContext
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.test.assertTrue

class MkdirTaskTest {

    @Test
    fun testExec() {
        val createTempFile = File.createTempFile(UUID.randomUUID().toString(), "test")
        val dir = createTempFile.parent + "/${UUID.randomUUID()}"
        val ctx = StreamContext()
        val task = MkdirTask()
        task.dir = dir
        task.execute(testId(), ctx)
        assertTrue(File(dir).isDirectory)
    }
}