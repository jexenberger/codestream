package io.codestream.module.utilmodule


import io.codestream.module.coremodule.testId
import io.codestream.runtime.StreamContext
import io.codestream.util.system
import org.junit.Test
import java.io.File
import kotlin.test.assertTrue
import kotlin.test.fail

class SavePropertiesTaskTest {

    @Test
    fun testExecute() {
        val task = SavePropertiesTask()
        val fileName = "${system.tempDir}/save-properties-test.properties"
        val ctx = StreamContext()
        ctx["test"] = "testValue"
        task.file = fileName
        task.properties = mapOf(
                "1" to 1,
                "2" to "#{test}"
        )
        task.execute(testId(), ctx)?.let { fail(it.toString()) }
        val file = File(fileName)
        assertTrue { file.exists() && file.isFile }
        println(file.readText())
    }

}