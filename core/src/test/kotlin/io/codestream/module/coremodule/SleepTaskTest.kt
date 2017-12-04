package io.codestream.module.coremodule


import io.codestream.core.defaultCondition
import org.junit.Test
import kotlin.test.fail

class SleepTaskTest {

    @Test
    fun testExecute() {
        val task = SleepTask()
        val coreModule = CoreModule()
        val (ctx, defn) = createTaskContext(coreModule, "sleep", condition = defaultCondition())
        task.execute(defn.id, ctx)?.let { fail(it.toString()) }
    }

}