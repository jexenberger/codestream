package io.codestream.core

import io.codestream.runtime.StreamContext

class MockModule(override val name: String = "mock",
                 override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<Executable>>> = mutableMapOf()) : Module {

    private val mockTaskId = TaskType(name, "mockTask")
    private val mockGroupId = TaskType(name, "mockGroupTask")
    private val id = TaskId("grp", "test")

    init {
        define {
            task(mockTaskId.name to taskFromClass(MockTask::class))
            groupTask(mockGroupId.name to groupFromClass(MockGroupTask::class))
        }
    }


    fun task(async: Boolean = false, condition: String? = null, ctx: StreamContext = StreamContext()): MockTask {
        return MockTask()
    }

    override fun toString(): String {
        return name
    }


}