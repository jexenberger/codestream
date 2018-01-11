package io.codestream.module.sshmodule

import io.codestream.core.Executable
import io.codestream.core.Factory
import io.codestream.core.Module
import io.codestream.core.TaskType
import io.codestream.runtime.classimpl.groupFromClass
import io.codestream.runtime.classimpl.taskFromClass

class SSHModule(override val name: String = "ssh",
                override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>> = mutableMapOf()) : Module {
    override val description: String
        get() = "Module for working with SSH connections"


    init {
        define {
            task(taskFromClass(ExecTask::class))
            task(taskFromClass(ScpFromTask::class))
            task(taskFromClass(ScpToTask::class))
            task(taskFromClass(ShellTask::class))

            groupTask(groupFromClass(SSHSessionGroupTask::class))
        }
    }
}