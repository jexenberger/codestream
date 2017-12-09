package io.codestream.core

class MockModule(override val name: String = "mock",
                 override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>> = mutableMapOf()) : Module {

    private val mockTaskId = TaskType(name, "mockTask")
    private val mockGroupId = TaskType(name, "mockGroupTask")
    private val id = TaskId("grp", "test")

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