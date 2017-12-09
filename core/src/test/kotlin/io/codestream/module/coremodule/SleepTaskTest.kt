package io.codestream.module.coremodule


import io.codestream.runtime.StreamContext
import org.junit.Test
import kotlin.test.fail

class SleepTaskTest {

    @Test
    fun testExecute() {
        val task = SleepTask()
        val ctx = StreamContext()
        task.execute(testId(), ctx)?.let { fail(it.toString()) }
    }

}