package io.codestream.module.sshmodule

import io.codestream.core.*

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