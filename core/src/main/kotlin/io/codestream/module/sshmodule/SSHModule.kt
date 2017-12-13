package io.codestream.module.sshmodule

import io.codestream.core.*

class SSHModule(override val name: String = "io",
                override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>> = mutableMapOf()) : Module {



    init {
        define {
            task("exec" to taskFromClass(ExecTask::class))
            task("scp-from" to taskFromClass(ScpFromTask::class))
            task("scp-to" to taskFromClass(ScpToTask::class))
            task("shell" to taskFromClass(ShellTask::class))
        }
    }
}