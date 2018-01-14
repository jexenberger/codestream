package io.codestream.module.httpmodule

import io.codestream.core.Executable
import io.codestream.core.Factory
import io.codestream.core.Module
import io.codestream.core.TaskType
import io.codestream.runtime.classimpl.DefaultTaskClassFactory

class HttpModule(override val name: String = "http",
                 override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>> = mutableMapOf()) : Module {
    override val description: String
        get() = "A set of utilities for performing HTTP operations"


    init {
        define {
            task(DefaultTaskClassFactory(GetTask::class))
            task(DefaultTaskClassFactory(PostTask::class))
            task(DefaultTaskClassFactory(PutTask::class))
            task(DefaultTaskClassFactory(DeleteTask::class))
        }
    }

}