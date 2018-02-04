package io.codestream.module.cryptomodule

import io.codestream.core.Executable
import io.codestream.core.Factory
import io.codestream.core.Module
import io.codestream.core.TaskType
import io.codestream.runtime.classimpl.taskFromClass

class CryptoModule(override val name: String = "crypto",
                   override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>> = mutableMapOf()) : Module {

    override val description = "A set of tasks for working cipher text, certificates and keys"

    init {
        define {
            task(taskFromClass(EncryptTask::class))
        }
    }

}