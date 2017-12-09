package io.codestream.module.coremodule

import io.codestream.runtime.StreamContext
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.test.assertTrue

class DumpTaskTest {

    @Test
    fun testExecute() {
        val file = File(System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID())
        file.deleteOnExit()
        val ctx = StreamContext()
        ctx["hello"] = "world"
        val dumpTask = DumpTask()
        dumpTask.file = file.absolutePath
        dumpTask.overwrite = true
        dumpTask.execute(testId(), ctx)
        assertTrue { file.exists() }
        println(file.readText())
    }
}