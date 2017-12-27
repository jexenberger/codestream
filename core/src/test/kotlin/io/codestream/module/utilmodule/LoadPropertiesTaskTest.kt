package io.codestream.module.utilmodule


import io.codestream.module.coremodule.testId
import io.codestream.runtime.StreamContext
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.fail

class LoadPropertiesTaskTest {

    @Test
    fun testExecute() {
        val ctx = StreamContext()
        val tempFile = File.createTempFile("load-properties", UUID.randomUUID().toString())
        val props = Properties()
        props["test"] = "test"
        props.store(tempFile.outputStream(), "Test")
        val task = LoadPropertiesTask()
        task.file = tempFile.absolutePath
        task.execute(testId(), ctx)?.let { fail(it.toString()) }
        assertEquals("test", ctx["test"])
    }

}