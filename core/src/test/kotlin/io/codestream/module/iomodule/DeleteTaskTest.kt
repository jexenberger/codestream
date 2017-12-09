package io.codestream.module.iomodule

import io.codestream.module.coremodule.testId
import io.codestream.runtime.StreamContext
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertNull

class DeleteTaskTest {


    @Test
    fun testExec() {
        val createTempFile = File.createTempFile(UUID.randomUUID().toString(), "test")
        val parentDir = createTempFile.parentFile.absolutePath + "/${UUID.randomUUID()}"
        val dirToDelete = File(parentDir)
        val child = File(parentDir, UUID.randomUUID().toString())
        child.mkdirs()
        val ctx = StreamContext()
        val deleteTask = DeleteTask()
        deleteTask.recursive = true
        deleteTask.src = parentDir
        val result = deleteTask.execute(testId(), ctx)
        assertNull(result, result.toString())
        assertFalse(dirToDelete.exists())
    }
}