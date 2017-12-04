package io.codestream.module.coremodule

import io.codestream.core.ExecutableDefinition
import io.codestream.core.TaskId
import io.codestream.core.scriptCondition
import io.codestream.runtime.StreamContext
import org.junit.Test

class WhileTaskTest {


    @Test
    fun testExec() {

        val ctx = StreamContext()
        val coreModule = CoreModule()
        val taskType = coreModule.typeOf("sleep")
        val id = TaskId("test", "test")
        val defn = ExecutableDefinition(taskType, id, condition = scriptCondition("true == false"))
        val task = WhileTask()
        val result = task.before(defn, ctx)
    }
}