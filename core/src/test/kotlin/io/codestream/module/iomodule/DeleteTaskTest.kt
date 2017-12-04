package io.codestream.module.iomodule

import io.codestream.core.defaultCondition
import io.codestream.module.coremodule.createTaskContext
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
        val (ctx, defn) = createTaskContext(IOModule(), "delete", condition = defaultCondition())
        val deleteTask = DeleteTask()
        deleteTask.recursive = true
        deleteTask.src = parentDir
        val result = deleteTask.execute(defn.id, ctx)
        assertNull(result, result?.let { it.toString() })
        assertFalse(dirToDelete.exists())
    }
}