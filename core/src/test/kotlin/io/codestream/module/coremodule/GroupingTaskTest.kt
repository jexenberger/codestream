package io.codestream.module.coremodule

import io.codestream.core.MockModule
import io.codestream.core.Module
import io.codestream.core.TaskId
import io.codestream.core.TaskType
import java.util.*

class GroupingTaskTest {

    private val mockType = TaskType("mock", "mockTask")
    private val groupType = TaskType("core", "group")

    init {
        Module += MockModule()
    }

    fun genId(id: String = UUID.randomUUID().toString()): TaskId {
        return TaskId("testGroup", "testStream", mockType, id)
    }
}