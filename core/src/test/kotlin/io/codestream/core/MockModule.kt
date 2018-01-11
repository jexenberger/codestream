package io.codestream.core

import io.codestream.runtime.classimpl.groupFromClass
import io.codestream.runtime.classimpl.taskFromClass

class MockModule(override val name: String = "mock",
                 override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>> = mutableMapOf()) : Module {
    override val description: String
        get() = "Mock module for testing"

    private val mockTaskId = TaskType(name, "mockTask")
    private val mockGroupId = TaskType(name, "mockGroupTask")
    private val id = TaskId("grp", "test", mockTaskId)

    init {
        define {
            task(mockTaskId.name to taskFromClass(MockTask::class))
            groupTask(mockGroupId.name to groupFromClass(MockGroupTask::class))
        }
    }


    override fun toString(): String {
        return name
    }


}